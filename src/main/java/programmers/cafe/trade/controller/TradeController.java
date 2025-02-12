package programmers.cafe.trade.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programmers.cafe.global.wrapper.response.ApiResponse;
import programmers.cafe.trade.domain.dto.request.*;
import programmers.cafe.trade.domain.dto.response.CancelResponseDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.service.user.TradeService;

import java.net.URI;
import java.util.List;

@Slf4j
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
    public ResponseEntity<ApiResponse<OrderResponseDto>> order(@RequestBody List<OrderRequestDto> dtoList) {
        OrderResponseDto orderResponseDto = service.transactionRequest(dtoList);
        ApiResponse<OrderResponseDto> apiResponse = new ApiResponse<>("Order created successfully", orderResponseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/deliver")
    public ResponseEntity<ApiResponse<DeliverResponseDto>> deliver(@RequestBody DeliverRequestDto deliverRequestDto) {
        return ResponseEntity.ok(new ApiResponse<>("Deliver success",service.processDelivery(deliverRequestDto)));
    }

    @PostMapping("/cancel/buy")
    public ResponseEntity<ApiResponse<PayResponseDto>> cancelOrderBeforePay(@RequestBody RefundRequestDto requestDto) {
        return ResponseEntity.ok(new ApiResponse<>("Cancel success when before pay",service.cancelTradeBeforePay(requestDto)));
    }
    @PostMapping("/cancel/pay")
    public ResponseEntity<ApiResponse<CancelResponseDto>> cancelOrderAfterPay(@RequestBody CancelRequestDto cancelRequestDto) {
        return ResponseEntity.ok(new ApiResponse<>("Cancel success when after pay", service.cancelTradeAfterPay(cancelRequestDto)));
    }
}
