package programmers.cafe.item.service.admin;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.item.domain.dto.request.ItemModifyInfoDto;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.dto.response.ItemResponseDto;
import programmers.cafe.item.domain.dto.response.ShowSimpleItem;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.repository.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static programmers.cafe.item.domain.entity.ItemCategory.*;

@SpringBootTest
class ItemServiceTest {
    @Autowired
    private ItemService service;


    private ItemRegisterDto itemDto1;
    private ItemRegisterDto itemDto2;
    private ItemRegisterDto itemDto3;
    private ItemRegisterDto itemDto4;

    @BeforeEach
    @Transactional
    void before() {
        service.clearAllItems();
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
    }

    @AfterEach
    void clearDatabase() {
        service.clearAllItems();
    }

    /**
     * register 상품 등록
     * showAllItem
     * showSingleItem
     * showCategoryItem
     * modifyInfo 상품 수정 -> 이름, 가격, 수량, 상세설명 / 상태 제외
     * modifyStatus 상품 수정 -> 상태만 변경. 호출 시 스위치 형식으로 변경됨. 판매중 <-> 품절
     * remove 상품 삭제
     */

    @Test
    @Transactional
    void register() {
        // given
        // when
        ItemResponseDto savedItem = service.register(itemDto1);

        // then
        assertThat(itemDto1.getName()).isEqualTo(savedItem.getName());
        assertThat(itemDto1.getPrice()).isEqualTo(2000);
    }

    @Test
    @Transactional
    void showAllItems() {
        // given
        // when
        ItemResponseDto savedItem1 = service.register(itemDto1);
        ItemResponseDto savedItem2 = service.register(itemDto2);
        ItemResponseDto savedItem3 = service.register(itemDto3);
        ItemResponseDto savedItem4 = service.register(itemDto4);

        List<ShowSimpleItem> searchResult = service.showAllItems();

        // then
        assertEquals(4, searchResult.size());
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem1.getItemId())));
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem2.getItemId())));
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem3.getItemId())));
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem4.getItemId())));
    }


    @Test
    @Transactional
    void showSingleItem() {
        // given
        // when
        ItemResponseDto savedItem1 = service.register(itemDto1);

        // then
        assertEquals(savedItem1.getItemId(), service.showSingleItem(savedItem1.getItemId()).getId());
        assertEquals(savedItem1.getName(), service.showSingleItem(savedItem1.getItemId()).getName());
        assertEquals(savedItem1.getPrice(), service.showSingleItem(savedItem1.getItemId()).getPrice());
    }

    @Test
    @Transactional
    void showCategoryItemsBeverage() {
        // given
        // when
        ItemResponseDto savedItem1 = service.register(itemDto1);
        ItemResponseDto savedItem2 = service.register(itemDto2);
        ItemResponseDto savedItem3 = service.register(itemDto3);
        ItemResponseDto savedItem4 = service.register(itemDto4);

        List<ShowSimpleItem> searchResult = service.showCategoryItem(BEVERAGE);

        // then
        // 음료 2가지는 모두 포함되어 있어야 함.
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem1.getItemId())));
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem2.getItemId())));

        assertTrue(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem1.getName())));
        assertTrue(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem2.getName())));

        // 디저트와 etc 는 검색 결과에 포함되지 않아야 함.
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem3.getItemId())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem4.getItemId())));
    }


    @Test
    @Transactional
    void showCategoryItemsDessert() {
        // given
        // when
        ItemResponseDto savedItem1 = service.register(itemDto1);
        ItemResponseDto savedItem2 = service.register(itemDto2);
        ItemResponseDto savedItem3 = service.register(itemDto3);
        ItemResponseDto savedItem4 = service.register(itemDto4);


        List<ShowSimpleItem> searchResult = service.showCategoryItem(DESSERT);

        // then
        // 음료와 etc 3가지는 검색 결과에 포함되지 않아야 함.
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem1.getItemId())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem2.getItemId())));

        assertFalse(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem1.getName())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem2.getName())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem4.getItemId())));

        // 디저트 1개만 포함되어 있어야 함.
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem3.getItemId())));
        assertEquals(1, searchResult.size());
    }

    @Test
    @Transactional
    void showCategoryItemsEtc() {
        // given
        // when
        ItemResponseDto savedItem1 = service.register(itemDto1);
        ItemResponseDto savedItem2 = service.register(itemDto2);
        ItemResponseDto savedItem3 = service.register(itemDto3);
        ItemResponseDto savedItem4 = service.register(itemDto4);

        List<ShowSimpleItem> searchResult = service.showCategoryItem(ETC);

        // then
        // 음료와 DESSERT 3가지는 검색 결과에 포함되지 않아야 함.
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem1.getItemId())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem2.getItemId())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem3.getItemId())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem1.getName())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem2.getName())));
        assertFalse(searchResult.stream().anyMatch(item -> item.getName().equals(savedItem3.getName())));

        // ETC 1개만 포함되어 있어야 함.
        assertTrue(searchResult.stream().anyMatch(item -> item.getId().equals(savedItem4.getItemId())));
        assertEquals(1, searchResult.size());
    }


    @Test
    @Transactional
    void modifyInfo() {
        // given
        ItemResponseDto savedItem1 = service.register(itemDto1);

        // when
        ItemModifyInfoDto itemDto = new ItemModifyInfoDto("IceAmericano", 1500, 999, "한국인이 좋아하는 아이스아메리카노");
        ItemResponseDto modified = service.modifyInfo(savedItem1.getItemId(), itemDto);

        // then
        assertTrue(modified.getItemId().equals(savedItem1.getItemId()));
        assertTrue(service.showSingleItem(savedItem1.getItemId()).getName().equals("IceAmericano"));
        assertTrue(service.showSingleItem(savedItem1.getItemId()).getPrice().equals(1500));
    }
}