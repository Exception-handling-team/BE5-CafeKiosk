package programmers.cafe.trade.service.user;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.global.exception.OverQuantityException;
import programmers.cafe.global.exception.PayRefusedException;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.dto.response.ItemResponseDto;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.repository.ItemRepository;
import programmers.cafe.item.service.ItemService;
import programmers.cafe.trade.domain.dto.request.DeliverRequestDto;
import programmers.cafe.trade.domain.dto.request.OrderRequestDto;
import programmers.cafe.trade.domain.dto.request.PayRequestDto;
import programmers.cafe.trade.domain.dto.response.DeliverResponseDto;
import programmers.cafe.trade.domain.dto.response.OrderResponseDto;
import programmers.cafe.trade.domain.dto.response.PayResponseDto;
import programmers.cafe.trade.domain.entity.TradeStatus;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static programmers.cafe.item.domain.entity.ItemCategory.*;


@SpringBootTest
class TradeServiceTest {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private ItemRegisterDto itemDto1;
    private ItemRegisterDto itemDto2;
    private ItemRegisterDto itemDto3;
    private ItemRegisterDto itemDto4;

    private ItemResponseDto savedItem1;
    private ItemResponseDto savedItem2;
    private ItemResponseDto savedItem3;
    private ItemResponseDto savedItem4;


    @BeforeEach
    void before() {
        itemService.clearAllItems();
        tradeService.clearAllTrade();

        itemDto1 = ItemRegisterDto.builder()
                .name("아메리카노")
                .price(2000)
                .description("맛있는 아메리카노")
                .status(ItemStatus.ON_SALE)
                .category(BEVERAGE)
                .quantity(10)
                .build();

        itemDto2 = ItemRegisterDto.builder()
                .name("모카")
                .price(3000)
                .description("달달한 모카")
                .status(ItemStatus.ON_SALE)
                .category(BEVERAGE)
                .quantity(20)
                .build();

        itemDto3 = ItemRegisterDto.builder()
                .name("감자빵")
                .price(2500)
                .description("강원도 감자로 만든 쫄깃한 감자빵")
                .status(ItemStatus.ON_SALE)
                .category(DESSERT)
                .quantity(5)
                .build();

        itemDto4 = ItemRegisterDto.builder()
                .name("스타벅스 텀블러")
                .price(30000)
                .description("스타벅스 한정판 텀블러")
                .status(ItemStatus.ON_SALE)
                .category(ETC)
                .quantity(1)
                .build();

        savedItem1 = itemService.register(itemDto1);
        savedItem2 = itemService.register(itemDto2);
        savedItem3 = itemService.register(itemDto3);
        savedItem4 = itemService.register(itemDto4);
    }
    @AfterEach
    void clearDatabase() {
        tradeService.clearAllTrade();
        itemService.clearAllItems();
    }

    @Test
    @Transactional
    void transactionRequest_Success() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 2), // 아메리카노 2개
                new OrderRequestDto(savedItem2.getItemId(), 3)  // 모카 3개
        );

        // when
        OrderResponseDto response = tradeService.transactionRequest(orderRequestDtos);

        // then
        assertNotNull(response);
        assertEquals(TradeStatus.BUY, response.getStatus());
        assertEquals(13000, response.getTotalPrice()); // 2000 * 2 + 3000 * 3 = 13000
        assertEquals(8, itemRepository.findById(savedItem1.getItemId()).get().getQuantity());
        assertEquals(17, itemRepository.findById(savedItem2.getItemId()).get().getQuantity());
    }

    @Test
    @Transactional
    void transactionRequest_OverQuantity() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 20) // 재고 10개 초과
        );

        // when & then
        assertThrows(OverQuantityException.class, () -> tradeService.transactionRequest(orderRequestDtos));
    }

    @Test
    @Transactional
    void processPayment_Success() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 2)
        );
        OrderResponseDto orderResponse = tradeService.transactionRequest(orderRequestDtos);
        PayRequestDto payRequestDto = new PayRequestDto(orderResponse.getTradeUUID(), 4000);

        // when
        PayResponseDto payResponse = tradeService.processPayment(payRequestDto);

        // then
        assertEquals(TradeStatus.PAY, payResponse.getStatus());
        assertEquals(4000, payResponse.getTotalPrice());
        assertEquals(8, itemRepository.findById(savedItem1.getItemId()).get().getQuantity());
    }

    @Test
    @Transactional
    void processPayment_AmountMismatch() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 2)
        );


        OrderResponseDto orderResponse = tradeService.transactionRequest(orderRequestDtos);
        PayRequestDto payRequestDto = new PayRequestDto(orderResponse.getTradeUUID(), 3000); // 결제 금액 불일치

        // when & then
        assertThrows(PayRefusedException.class, () -> tradeService.processPayment(payRequestDto));
        assertEquals(10, itemRepository.findById(savedItem1.getItemId()).get().getQuantity());
    }

    @Test
    @Transactional
    void processDelivery_Success() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 2),
                new OrderRequestDto(savedItem2.getItemId(), 2),
                new OrderRequestDto(savedItem3.getItemId(), 2)
        );
        OrderResponseDto orderResponse = tradeService.transactionRequest(orderRequestDtos);
        tradeService.processPayment(new PayRequestDto(orderResponse.getTradeUUID(), 15000));

        DeliverRequestDto deliverRequestDto = new DeliverRequestDto(orderResponse.getTradeUUID());

        // when
        DeliverResponseDto deliverResponse = tradeService.processDelivery(deliverRequestDto);

        // then
        assertEquals(TradeStatus.END, deliverResponse.getStatus());
    }

    @Test
    void processDelivery_BeforePayment() {
        // given
        List<OrderRequestDto> orderRequestDtos = List.of(
                new OrderRequestDto(savedItem1.getItemId(), 2)
        );
        OrderResponseDto orderResponse = tradeService.transactionRequest(orderRequestDtos);

        DeliverRequestDto deliverRequestDto = new DeliverRequestDto(orderResponse.getTradeUUID());

        // when & then
        assertThrows(PayRefusedException.class, () -> tradeService.processDelivery(deliverRequestDto));
    }
}