package programmers.cafe.trade.service.user;

import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.exception.*;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.repository.ItemRepository;
import programmers.cafe.trade.domain.dto.request.DeliverRequestDto;
import programmers.cafe.trade.domain.dto.request.PayRequestDto;
import programmers.cafe.trade.domain.dto.request.OrderRequestDto;
import programmers.cafe.trade.domain.dto.request.RefundRequestDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.domain.entity.Trade;
import programmers.cafe.trade.domain.entity.TradeItem;
import programmers.cafe.trade.domain.entity.TradeStatus;
import programmers.cafe.trade.portone.domain.dto.RefundMessageResponse;
import programmers.cafe.trade.portone.service.PortoneService;
import programmers.cafe.trade.repository.TradeRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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


        Trade trade = Trade.builder()
                .tradeStatus(BUY) // 거래 상태 초기화
                .build();

        List<TradeItem> tradeItems = dtoList.stream().map(dto -> {
            // Step 3: 각 TradeItem 생성
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없음."));

            System.out.println("origin item quantity : " + item.getQuantity());
            System.out.println("purchase quantity : " + dto.getQuantity());
            System.out.println("remain item quantity : " + (item.getQuantity() - dto.getQuantity()));
            // 재고 차감
            item.setQuantity(item.getQuantity() - dto.getQuantity());

            item.autoCheckQuantityForSetStatus(); // 수량을 확인하여 자동으로 판매중 / 품절의 상태를 변경하는 엔티티 메서드


            System.out.println("after transaction remain item quantity : " + item.getQuantity());

            // TradeItem 생성
            return TradeItem.builder()
                    .trade(trade)
                    .item(item)
                    .quantity(dto.getQuantity())
                    .build();
        }).toList();

        // Trade와 TradeItem 연결
        trade.setTradeItems(tradeItems);

        trade.setTotalPrice(calculateTotalPrice(tradeItems));


        String purchaseUUID;

        try {
           purchaseUUID = portoneService.prePurchase(new BigDecimal(trade.getTotalPrice()));
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        trade.setTradeUUid(purchaseUUID);

        // Trade 저장
        tradeRepository.save(trade);

        // Step 4: 응답 객체 생성 및 반환
        return new OrderResponseDto(
                trade.getId(),
                trade.getTradeStatus(),
                calculateTotalPrice(tradeItems), // 총 가격 계산 메서드
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
    public PayResponseDto cancelTrade(RefundRequestDto requestDto) {



        Trade trade = tradeRepository.findByTradeUUid(requestDto.getTradeUUID())
                .orElseThrow(() -> new TradeNotFoundException("해당 거래를 찾을 수 없음."));


        TradeStatus status = trade.getTradeStatus();


        if (status.equals(REFUSED)) {
            throw new TradeCommonException("이미 취소된 거래입니다.");
        }
        if (status.equals(END)) {
            throw new TradeCommonException("이미 완료된 거래입니다.");
        }

        //실제 포트원 결제 취소 로직 실행
        BigDecimal amount = requestDto.getAmount();
        if (trade.getTotalPrice() < amount.intValue()) {
            throw new PayRefusedException("결제 금액보다 환불금액이 더 큽니다.");
        }

        portoneService.refund(trade.getId(),requestDto.getAmount());

        //상품 관리 수량, 상태 복구 로직
        System.out.println("[test] change status to refused : roll back quantity");
        trade.setTradeStatus(REFUSED);
        System.out.println("[test] method call : roll back quantity");
        rollBackProductQuantity(trade);

        return new PayResponseDto(
                trade.getTradeUUid(),
                trade.getTradeStatus(),
                trade.getTotalPrice()
        );
    }
}
