package programmers.cafe.trade.service.user;

import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.global.exception.*;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.repository.ItemRepository;
import programmers.cafe.trade.domain.dto.request.*;
import programmers.cafe.trade.domain.dto.request.CancelRequestDto.CancelItemRequest;
import programmers.cafe.trade.domain.dto.response.CancelResponseDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.domain.entity.Trade;
import programmers.cafe.trade.domain.entity.TradeItem;
import programmers.cafe.trade.domain.entity.TradeStatus;
import programmers.cafe.trade.portone.service.PortoneService;
import programmers.cafe.trade.repository.TradeRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static programmers.cafe.trade.domain.entity.TradeStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;
    private final PortoneService portoneService;

    /**
     * 구매 요청 -> 거래 가능한 수량 범위 이내 : BUY
     * 결제 성공 -> 상태 설정 PAY / 결제 실패 -> 상태 설정 REFUSED
     * 상품 수령 후 -> 상태 설정 END
     */
    @Transactional
    public OrderResponseDto transactionRequest(List<OrderRequestDto> dtoList) {
        boolean stockValidResult = dtoList.stream().allMatch(this::stockValidCheck);
        if (!stockValidResult) {
            throw new OverQuantityException("요청한 상품 중 재고가 부족한 상품이 있습니다.");
        }

        // Step 1: Trade 객체 생성
        Trade trade = Trade.builder()
                .tradeStatus(BUY) // 거래 상태 초기화
                .tradeItems(new ArrayList<>()) // 리스트 초기화
                .build();

        // Step 2: TradeItem 생성 및 연결
        for (OrderRequestDto dto : dtoList) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없음."));

            // 재고 차감
            item.setQuantity(item.getQuantity() - dto.getQuantity());
            item.autoCheckQuantityForSetStatus();

            // TradeItem 생성 및 연결
            TradeItem tradeItem = TradeItem.builder()
                    .trade(trade) // 연관관계 설정
                    .item(item)
                    .quantity(dto.getQuantity())
                    .build();

            tradeItem.setPrice();

            trade.addTradeItem(tradeItem); // 연관관계 설정
        }

        // Step 3: 총 가격 계산
        trade.setTotalPrice(calculateTotalPrice(trade.getTradeItems()));

        // Step 4: 결제 요청
        String purchaseUUID;
        try {
            purchaseUUID = portoneService.prePurchase(new BigDecimal(trade.getTotalPrice()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trade.setTradeUUid(purchaseUUID);

        // Step 5: Trade 저장 (cascade 설정이 되어있다면 TradeItem도 함께 저장됨)
        tradeRepository.save(trade);

        // Step 6: 응답 객체 생성 및 반환
        return new OrderResponseDto(
                trade.getId(),
                trade.getTradeStatus(),
                trade.getTotalPrice(),
                trade.getTradeUUid()
        );
    }

    // 결제 성공
    @Transactional
    public PayResponseDto processPayment(PayRequestDto requestDto) {

        // Step 1: Trade 조회
        Trade trade = tradeRepository.findByTradeUUid(requestDto.getTradeUUID())
                .orElseThrow(() -> new TradeNotFoundException("해당 거래를 찾을 수 없습니다: " + requestDto.getTradeUUID()));

        // Step 2: 이미 결제 완료된 거래인지 확인
        try {
            if (trade.getTradeStatus() == PAY) {
                throw new PayRefusedException("이미 결제가 완료된 거래 입니다.");
            } else if (trade.getTradeStatus() == REFUSED) {
                throw new PayRefusedException("주문이 이미 취소된 거래 입니다.");
            }

            // Step 3: 결제 금액과 지불 금액 일치 여부 확인 로직
            if (!trade.getTotalPrice().equals(requestDto.getPayment())) {
                throw new PayRefusedException("주문 금액과 결제 금액이 다릅니다.");
            }
        } catch (PayRefusedException e) {
            trade.setTradeStatus(REFUSED);
            rollBackProductQuantity(trade);
            throw e;
        }




        // Step 4: 결제 처리 로직
        trade.setTradeStatus(PAY);

        // Step 4: 응답 객체 생성 및 반환
        return new PayResponseDto(
                trade.getTradeUUid(),
                trade.getTradeStatus(),
                trade.getTotalPrice()
        );
    }

    @Transactional
    public void rollBackProductQuantity(Trade trade) {
        System.out.println("[test] method called : roll back quantity");
        List<TradeItem> tradeItems = trade.getTradeItems();

        tradeItems.stream().forEach(tradeItem -> {

            System.out.println("set quantity : " + tradeItem.getItem().getQuantity() + tradeItem.getQuantity());
            tradeItem.getItem().setQuantity(tradeItem.getItem().getQuantity() + tradeItem.getQuantity());
            tradeItem.getItem().autoCheckQuantityForSetStatus();
        });
    }

    // 상품 수령
    @Transactional
    public DeliverResponseDto processDelivery(DeliverRequestDto requestDto) {
        Trade trade = tradeRepository.findByTradeUUid(requestDto.getTradeUUID())
                .orElseThrow(() -> new TradeNotFoundException("해당 거래를 찾을 수 없습니다: " + requestDto.getTradeUUID()));

        // Step 2: 이미 결제 완료된 거래인지 확인
        if (trade.getTradeStatus() != PAY) {
            if (trade.getTradeStatus() == END) {
                throw new PayRefusedException("이미 수령된 상품 입니다.");
            } else if (trade.getTradeStatus() == BUY) {
                throw new PayRefusedException("결제가 완료되지 않은 주문 입니다.");
            } else if (trade.getTradeStatus() == REFUSED) {
                throw new PayRefusedException("취소된 주문 입니다.");
            } else {
                throw new UnknownError("unknown exception");
            }
        }

        trade.setTradeStatus(END);

        return new DeliverResponseDto (
                trade.getTradeUUid(),
                trade.getTradeStatus()
        );
    }

    @Transactional
    public void clearAllTrade() {
        tradeRepository.deleteAll();
    }


    // 보조 메서드
    @Transactional
    public Integer calculateTotalPrice(List<TradeItem> tradeItems) {
        return tradeItems.stream()
                .mapToInt(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }


    private boolean stockValidCheck(OrderRequestDto request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(()-> new ItemNotFoundException("해당 상품을 찾을 수 없음."));
        Integer reqQuantity = request.getQuantity();
        Integer itemStock = item.getQuantity();
        if (reqQuantity < 0) {
            throw new OverQuantityException("구매 수량은 0 보다 작을 수 없습니다.");
        }

        if (item.getStatus().equals(ItemStatus.SOLD_OUT)) {
            return false;
        }
        return reqQuantity <= itemStock;
    }

    @Transactional
    public PayResponseDto cancelTradeBeforePay(RefundRequestDto requestDto) {
        Trade trade = tradeRepository.findByTradeUUid(requestDto.getTradeUUID())
                .orElseThrow(() -> new TradeNotFoundException("해당 거래를 찾을 수 없음."));


        TradeStatus status = trade.getTradeStatus();


        if (status.equals(REFUSED)) {
            throw new TradeCommonException("이미 취소된 거래입니다.");
        }
        if (status.equals(END)) {
            throw new TradeCommonException("이미 완료된 거래입니다.");
        }
        if (status.equals(PAY)) {
            throw new TradeCommonException("결제가 이루어진 거래입니다. 판매자에게 문의하세요.");
        }

        //상품 관리 수량, 상태 복구 로직
        rollBackProductQuantity(trade);
        trade.setTradeStatus(REFUSED);

        return new PayResponseDto(
                trade.getTradeUUid(),
                trade.getTradeStatus(),
                trade.getTotalPrice()
        );
    }

    @Transactional
    public CancelResponseDto cancelTradeAfterPay(CancelRequestDto cancelRequestDto) {
        BigDecimal refundAmount = BigDecimal.ZERO; // 총 환불 금액

        Trade trade = tradeRepository.findByTradeUUidWithItems(cancelRequestDto.getTradeUUID())
                .orElseThrow(() -> new TradeNotFoundException("해당 거래를 찾을 수 없음."));

        // 거래 상태 검증
        TradeStatus status = trade.getTradeStatus();
        if (status.equals(REFUSED) || status.equals(END)) {
            throw new TradeCommonException("이미 취소되었거나 완료된 거래입니다.");
        }

        if (status.equals(BUY)) {
            throw new TradeCommonException("아직 결제가 이루어지지 않는 거래는 판매자가 취소 할 수 없습니다.");
        }

        // 취소할 아이템 목록 가져오기

        List<TradeItem> tradeItems = trade.getTradeItems(); // 이미 fetch join 되어 있음
        System.out.println(tradeItems);

        Map<Long, TradeItem> tradeItemMap = tradeItems.stream()
                .collect(Collectors.toMap(ti -> ti.getItem().getId(), Function.identity()));
        System.out.println(tradeItemMap);

        // 취소 요청 검증
        for (CancelRequestDto.CancelItemRequest requestItem : cancelRequestDto.getCancelItemList()) {
            TradeItem tradeItem = tradeItemMap.get(requestItem.getItemId());

            if (tradeItem == null) {
                throw new TradeCommonException("취소 요청한 아이템이 거래 내역에 없음: " + requestItem.getItemId());
            }

            if (tradeItem.getQuantity() < requestItem.getQuantity()) {
                throw new TradeCommonException("취소 요청 수량이 거래된 수량보다 많음: " + requestItem.getItemId());
            }

            //취소한 수량만큼 Item 재고 늘리기
            Item item = tradeItem.getItem();
            item.setQuantity(item.getQuantity() + requestItem.getQuantity());
            item.autoCheckQuantityForSetStatus(); // 품절/판매중 상태 자동 변경

            //환불 금액 계산 (단가 × 취소 수량)
            BigDecimal itemPrice = new BigDecimal(item.getPrice());
            refundAmount = refundAmount.add(itemPrice.multiply(BigDecimal.valueOf(requestItem.getQuantity())));

            //TradeItem 수량 업데이트 (부분 취소를 고려)
            tradeItem.setQuantity(tradeItem.getQuantity() - requestItem.getQuantity());
        }

        // 전체 취소 시 상태 변경
        boolean allItemsCancelled = tradeItems.stream().allMatch(tradeItem -> tradeItem.getQuantity() == 0);
        if (allItemsCancelled) {
            trade.setTradeStatus(REFUSED);
        }

        System.out.println(refundAmount);


        //실제 포트원 결제 취소 로직 실행
        portoneService.refund(trade.getId(),refundAmount);


        return new CancelResponseDto(trade.getTradeUUid(), "취소 완료");
    }

}
