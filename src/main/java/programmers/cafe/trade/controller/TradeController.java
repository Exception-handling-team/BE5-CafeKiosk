package programmers.cafe.trade.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import programmers.cafe.trade.domain.dto.request.DeliverRequestDto;
import programmers.cafe.trade.domain.dto.request.PayRequestDto;
import programmers.cafe.trade.domain.dto.request.OrderRequestDto;
import programmers.cafe.trade.domain.dto.request.RefundRequestDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.service.user.TradeService;

import java.util.List;

@Tag(name = "상품 주문", description = "상품 주문, 결제, 수령")
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class TradeController {

    private final TradeService service;

    /**
     * service             : url endpoint        :설명
     * transactionRequest  : /order              :상품 등록   trade 생성, 저장
     * pay                 : /order/pay          :상품 구매   trade 상태 변경 : BUY -> PAY
     * deliver             : /order/deliver      :상품 수령 완료 trade 상태 변경 : PAY -> END
     *
     * 판매자 (가게) 측에서 거절하기 기능 추가
     *
     * reject
     */

    @PostMapping
    public ResponseEntity<OrderResponseDto> order(@RequestBody List<OrderRequestDto> dtoList) {
        return ResponseEntity.ok(service.transactionRequest(dtoList));
    }

//    @PostMapping("/pay")
//    public ResponseEntity<PayResponseDto> pay(PayRequestDto payRequestDto) {
//        return ResponseEntity.ok(service.processPayment(payRequestDto));
//    }

    @PostMapping("/deliver")
    public ResponseEntity<DeliverResponseDto> deliver(DeliverRequestDto deliverRequestDto) {
        return ResponseEntity.ok(service.processDelivery(deliverRequestDto));
    }

    @PostMapping("/cancel")
    public ResponseEntity<PayResponseDto> cancelOrderPayment(RefundRequestDto requestDto) {
        return ResponseEntity.ok(service.cancelTrade(requestDto));
    }
}
