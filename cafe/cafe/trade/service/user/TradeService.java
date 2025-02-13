package programmers.cafe.trade.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.exception.ItemNotFoundException;
import programmers.cafe.exception.OverQuantityException;
import programmers.cafe.exception.PayRefusedException;
import programmers.cafe.exception.TradeNotFoundException;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.repository.ItemRepository;
import programmers.cafe.trade.domain.dto.request.DeliverRequestDto;
import programmers.cafe.trade.domain.dto.request.PayRequestDto;
import programmers.cafe.trade.domain.dto.request.OrderRequestDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.domain.entity.Trade;
import programmers.cafe.trade.domain.entity.TradeItem;
import programmers.cafe.trade.repository.TradeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static programmers.cafe.trade.domain.entity.TradeStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;

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
                .tradeUUid(UUID.randomUUID().toString()) // 고유 거래 식별자 생성
                .tradeTime(LocalDateTime.now()) // 거래 시간 설정
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

            List<TradeItem> tradeItems = trade.getTradeItems();

            tradeItems.stream().forEach(tradeItem -> tradeItem.getItem().setQuantity(tradeItem.getItem().getQuantity() + tradeItem.getQuantity()));

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
            } else {
                throw new PayRefusedException("취소된 주문 입니다.");
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
    private Integer calculateTotalPrice(List<TradeItem> tradeItems) {
        return tradeItems.stream()
                .mapToInt(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }


    private boolean stockValidCheck(OrderRequestDto request) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(()-> new ItemNotFoundException("해당 상품을 찾을 수 없음."));
        Integer reqQuantity = request.getQuantity();
        Integer itemStock = item.getQuantity();

        return reqQuantity <= itemStock;
    }
}
