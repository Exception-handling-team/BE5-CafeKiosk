package programmers.cafe.item.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemCategory;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.service.ItemService;
import java.nio.charset.StandardCharsets;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Item Test")
class ItemAdminControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemService itemService;

    private ResultActions registerRequest(String name, Integer price, String description, String ItemStatus, String category, Integer quantity) throws Exception {
        return mvc
                .perform(
                        post("/admin/register")
                                .content(
                                        """
                                                {
                                                  "name": "%s",
                                                  "price": "%d",
                                                  "description": "%s",
                                                  "status": "%s",
                                                  "category": "%s",
                                                  "quantity": "%d"
                                                }
                                                """
                                                .formatted(name, price, description, ItemStatus, category, quantity)
                                                .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                ).andDo(print());
    }


    private void checkItem(ResultActions resultActions, Item item, ItemStatus itemStatus) throws Exception {
        resultActions
                .andExpect(jsonPath("$.data.itemId").value(item.getId()))
                .andExpect(jsonPath("$.data.name").value(item.getName()))
                .andExpect(jsonPath("$.data.price").value(item.getPrice()))
                .andExpect(jsonPath("$.data.quantity").value(item.getQuantity()))
                .andExpect(jsonPath("$.data.description").value(item.getDescription()));

        Assertions.assertEquals(item.getStatus(), itemStatus);
    }

    @Test
    @DisplayName("상품 등록 테스트 - 성공")
    void registerItem() throws Exception {
        // given
        String name = "test";
        Integer price = 5000;
        String description = "this is test item";
        String itemStatus = ItemStatus.ON_SALE.name();
        String itemCategory = ItemCategory.BEVERAGE.name();
        Integer quantity = 7;

        // when
        ResultActions resultActions = registerRequest(name, price, description, itemStatus, itemCategory, quantity);

        Item item = itemService.getLatestItem().get();

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemAdminController.class))
                .andExpect(handler().methodName("itemRegister"))
                .andExpect(jsonPath("$.message").value("new item register success"));

        checkItem(resultActions, item, ItemStatus.ON_SALE);
    }
}