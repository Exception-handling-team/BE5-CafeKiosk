package programmers.cafe.trade.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.repository.ItemRepository;
import programmers.cafe.item.service.ItemService;
import programmers.cafe.trade.domain.entity.Trade;
import programmers.cafe.trade.domain.entity.TradeItem;
import programmers.cafe.trade.domain.entity.TradeStatus;
import programmers.cafe.trade.portone.service.PortoneService;
import programmers.cafe.trade.repository.TradeItemRepository;
import programmers.cafe.trade.repository.TradeRepository;
import programmers.cafe.trade.service.user.TradeService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Trade Test")
class TradeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeItemRepository tradeItemRepository;

    @MockBean
    private PortoneService portoneService;


    private ResultActions orderRequest(Long item1Id, Integer item1Quantity, Long item2Id, Integer item2Quantity) throws Exception {
        return mvc
                .perform(
                        post("/order")
                                .content(
                                        """
                                                [
                                                {
                                                    "itemId" : "%s",
                                                    "quantity" : "%s"
                                                },
                                                {
                                                    "itemId" : "%s",
                                                    "quantity" : "%s"
                                                }
                                                ]
                                                """
                                                .formatted(item1Id, item1Quantity, item2Id, item2Quantity)
                                                .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                ).andDo(print());
    }

    private void checkTrade(ResultActions resultActions, Trade trade) throws Exception {
        resultActions
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.tradeId").value(trade.getId()))
                .andExpect(jsonPath("$.data.status").value(trade.getTradeStatus().name()))
                .andExpect(jsonPath("$.data.totalPrice").value(trade.getTotalPrice()))
                .andExpect(jsonPath("$.data.tradeUUID").value(trade.getTradeUUID()));
    }

    @Test
    @DisplayName("주문 테스트 - 성공")
    void orderTestSuccess() throws Exception {
        Long id1 = 1L;
        Integer q1 = 2;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions resultActions = orderRequest(id1, q1, id2, q2);

        Trade trade = tradeService.getLatestTrade().get();

        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));


        checkTrade(resultActions, trade);

    }

    @Test
    @DisplayName("주문 테스트 - 실패 - 수량 초과")
    void orderTestFail1() throws Exception {
        Long id1 = 1L;
        Integer q1 = 40;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions resultActions = orderRequest(id1, q1, id2, q2);

        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("요청한 상품 중 재고가 부족한 상품이 있습니다."));
    }

    @Test
    @DisplayName("주문 테스트 - 실패 - 존재 하지 않는 상품 주문")
    void orderTestFail2() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 9L;
        Integer q2 = 5;

        ResultActions resultActions = orderRequest(id1, q1, id2, q2);

        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    private void tradeSetPay(Trade trade) {
        trade.setTradeStatus(TradeStatus.PAY);
    }
    private void tradeSetRefused(Trade trade) {
        trade.setTradeStatus(TradeStatus.REFUSED);
    }
    private void tradeSetEnd(Trade trade) {
        trade.setTradeStatus(TradeStatus.END);
    }

    private ResultActions cancelNotPayRequest(String tradeUUID) throws Exception {
        return mvc
                .perform(
                        post("/order/cancel/buy")
                                .content(
                                        """
                                                {
                                                    "tradeUUID" : "%s"
                                                }
                                                """
                                                .formatted(tradeUUID)
                                                .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                ).andDo(print());
    }


    @Test
    @DisplayName("결제 전 취소 테스트 - 성공")
    void cancelTest() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        ResultActions cancelRequest = cancelNotPayRequest(trade.getTradeUUID());

        cancelRequest
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderBeforePay"))
                .andExpect(jsonPath("$.message").value("Cancel success when before pay"))
                .andExpect(jsonPath("$.data.tradeId").value(trade.getId()))
                .andExpect(jsonPath("$.data.tradeUUID").value(trade.getTradeUUID()))
                .andExpect(jsonPath("$.data.status").value(TradeStatus.REFUSED.name()))
                .andExpect(jsonPath("$.data.totalPrice").value(trade.getTotalPrice()));

        List<TradeItem> tradeItems = trade.getTradeItems();// trade 객체가 아이템 목록을 가지고 있다고 가정

        for (TradeItem tradeItem : tradeItems) {
            Item realItem = itemService.findOne(tradeItem.getItem().getId());
            if (tradeItem.getId().equals(id1)) {
                assertEquals(10, realItem.getQuantity());
            }
            if (tradeItem.getId().equals(id2)) {
                assertEquals(20, realItem.getQuantity());
            }
        }
    }

    @Test
    @DisplayName("결제 전 취소 테스트 - 실패 : uuid 값 오류")
    void cancelTestFail1() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        ResultActions cancelRequest = cancelNotPayRequest(trade.getTradeUUID()+ UUID.randomUUID());

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderBeforePay"))
                .andExpect(jsonPath("$.message").value("해당 거래를 찾을 수 없음."));
    }

    @Test
    @DisplayName("결제 전 취소 테스트 - 실패 : REFUSED -> REFUSED")
    void cancelTestFail2() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();

        tradeSetRefused(trade);

        ResultActions cancelRequest = cancelNotPayRequest(trade.getTradeUUID());

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderBeforePay"))
                .andExpect(jsonPath("$.message").value("이미 취소된 거래입니다."));
    }

    @Test
    @DisplayName("결제 전 취소 테스트 - 실패 : END -> REFUSED")
    void cancelTestFail3() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();

        tradeSetEnd(trade);

        ResultActions cancelRequest = cancelNotPayRequest(trade.getTradeUUID());

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderBeforePay"))
                .andExpect(jsonPath("$.message").value("이미 완료된 거래입니다."));
    }

    @Test
    @DisplayName("결제 전 취소 테스트 - 실패 : PAY -> REFUSED")
    void cancelTestFail4() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();

        tradeSetPay(trade);

        ResultActions cancelRequest = cancelNotPayRequest(trade.getTradeUUID());

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderBeforePay"))
                .andExpect(jsonPath("$.message").value("결제가 이루어진 거래입니다. 판매자에게 문의하세요."));
    }

    private ResultActions cancelAfterPayRequest(String tradeUUID, Long item1Id, Integer item1Quantity, Long item2Id, Integer item2Quantity) throws Exception {
        return mvc
                .perform(
                        post("/order/cancel/pay")
                                .content(
                                        """
                                                {
                                                    "tradeUUID" : "%s",
                                                    "cancelItemList" :
                                                    [
                                                        {
                                                            "itemId" : "%s",
                                                            "quantity" : "%s"
                                                        },
                                                        {
                                                            "itemId" : "%s",
                                                            "quantity" : "%s"
                                                        }
                                                    ]
                                                }
                                                """
                                                .formatted(tradeUUID, item1Id, item1Quantity, item2Id, item2Quantity)
                                                .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                ).andDo(print());
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 성공")
    void refundTest() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetPay(trade);
        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, id2, 2);

        cancelRequest
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("Cancel success when after pay"))
                .andExpect(jsonPath("$.data.tradeUUID").value(tradeUUID))
                .andExpect(jsonPath("$.data.refundPrice").value(10000));


        assertEquals(itemRepository.findById(1L).get().getQuantity(), 2);
        assertEquals(itemRepository.findById(2L).get().getQuantity(), 17);
        assertEquals(tradeItemRepository.findById(1L).get().getQuantity(), 8);
        assertEquals(tradeItemRepository.findById(2L).get().getQuantity(), 3);
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : UUID 조회 불가")
    void refundTest1() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetPay(trade);
        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID + UUID.randomUUID(), id1, 2, id2, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("해당 거래를 찾을 수 없음."));
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : REFUSED -> REFUSED")
    void refundTest2() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetRefused(trade);
        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, id2, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("이미 취소되었거나 완료된 거래입니다."));
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : END -> REFUSED")
    void refundTest3() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetEnd(trade);
        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, id2, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("이미 취소되었거나 완료된 거래입니다."));
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : BUY -> REFUSED")
    void refundTest4() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        checkTrade(orderedRequest, trade);

        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, id2, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("아직 결제가 이루어지지 않는 거래는 판매자가 취소 할 수 없습니다."));
    }


    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : 존재 하지 않는 상품 취소 요청")
    void refundTest5() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        checkTrade(orderedRequest, trade);
        tradeSetPay(trade);

        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, 9L, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("취소 요청한 아이템이 거래 내역에 없음: " + 9L));
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : 주문 하지 않는 상품 취소 요청")
    void refundTest6() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        checkTrade(orderedRequest, trade);
        tradeSetPay(trade);

        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, 3L, 2);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("취소 요청한 아이템이 거래 내역에 없음: " + 3L));
    }

    @Test
    @DisplayName("결제 후 취소 테스트 - 실패 : 주문한 상품 개수 초과 취소 요청")
    void refundTest7() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        checkTrade(orderedRequest, trade);
        tradeSetPay(trade);

        String tradeUUID = trade.getTradeUUID();

        doNothing().when(portoneService).refund(anyLong(), any());

        ResultActions cancelRequest = cancelAfterPayRequest(tradeUUID, id1, 2, 2L, 10);

        cancelRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("cancelOrderAfterPay"))
                .andExpect(jsonPath("$.message").value("취소 요청 수량이 거래된 수량보다 많음: " + id2));
    }


    private ResultActions deliverRequest(String tradeUUID) throws Exception {
        return mvc
                .perform(
                        post("/order/deliver")
                                .content(
                                        """
                                        {
                                            "tradeUUID" : "%s"
                                        }
                                        """
                                                .formatted(tradeUUID)
                                                .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                ).andDo(print());
    }

    @Test
    @DisplayName("상품 수령 완료 - 성공")
    void deliverRequest() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetPay(trade);
        String tradeUUID = trade.getTradeUUID();

        ResultActions deliverRequest = deliverRequest(tradeUUID);

        deliverRequest
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("deliver"))
                .andExpect(jsonPath("$.message").value("Deliver success"))
                .andExpect(jsonPath("$.data.tradeUUID").value(tradeUUID))
                .andExpect(jsonPath("$.data.status").value(TradeStatus.END.name()));
    }

    @Test
    @DisplayName("상품 수령 완료 - 실패 : UUID 조회 불가")
    void deliverRequest2() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetPay(trade);
        String tradeUUID = trade.getTradeUUID() + UUID.randomUUID();
        ResultActions deliverRequest = deliverRequest(tradeUUID);

        deliverRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("deliver"))
                .andExpect(jsonPath("$.message").value("해당 거래를 찾을 수 없습니다: " + tradeUUID));

    }

    @Test
    @DisplayName("상품 수령 완료 - 실패 : END -> END")
    void deliverRequest3() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetEnd(trade);
        String tradeUUID = trade.getTradeUUID();
        ResultActions deliverRequest = deliverRequest(tradeUUID);

        deliverRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("deliver"))
                .andExpect(jsonPath("$.message").value("이미 수령된 상품 입니다."));
    }

    @Test
    @DisplayName("상품 수령 완료 - 실패 : BUY -> END")
    void deliverRequest4() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        String tradeUUID = trade.getTradeUUID();
        ResultActions deliverRequest = deliverRequest(tradeUUID);

        deliverRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("deliver"))
                .andExpect(jsonPath("$.message").value("결제가 완료되지 않은 주문 입니다."));

    }

    @Test
    @DisplayName("상품 수령 완료 - 실패 : REFUSED -> END")
    void deliverRequest5() throws Exception {
        Long id1 = 1L;
        Integer q1 = 10;
        Long id2 = 2L;
        Integer q2 = 5;

        ResultActions orderedRequest = orderRequest(id1, q1, id2, q2);

        orderedRequest
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("order"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        Trade trade = tradeService.getLatestTrade().get();
        tradeSetRefused(trade);
        String tradeUUID = trade.getTradeUUID();
        ResultActions deliverRequest = deliverRequest(tradeUUID);

        deliverRequest
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(TradeController.class))
                .andExpect(handler().methodName("deliver"))
                .andExpect(jsonPath("$.message").value("취소된 주문 입니다."));
    }
}