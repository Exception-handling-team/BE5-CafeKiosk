package programmers.cafe.global.dataInit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemCategory;
import programmers.cafe.item.domain.entity.ItemStatus;
import programmers.cafe.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpClient;

import static programmers.cafe.item.domain.entity.ItemCategory.DESSERT;
import static programmers.cafe.item.domain.entity.ItemCategory.ETC;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        // 초기 데이터가 이미 존재하는지 확인 (중복 삽입 방지)
        if (itemRepository.count() == 0) {
            Item item1 = Item.builder()
                    .name("아메리카노")
                    .price(2000)
                    .description("맛있는 아메리카노")
                    .status(ItemStatus.ON_SALE)
                    .category(ItemCategory.BEVERAGE)
                    .quantity(10)
                    .build();

            Item item2 = Item.builder()
                    .name("모카")
                    .price(3000)
                    .description("달달한 모카")
                    .status(ItemStatus.ON_SALE)
                    .category(ItemCategory.BEVERAGE)
                    .quantity(20)
                    .build();

            Item item3 = Item.builder()
                    .name("쿠키")
                    .price(1500)
                    .description("직접 구운 초코 쿠키")
                    .status(ItemStatus.ON_SALE)
                    .category(ItemCategory.DESSERT)
                    .quantity(3)
                    .build();

            Item item4 = Item.builder()
                    .name("마카롱")
                    .price(2500)
                    .description("달달한 뚱카롱")
                    .status(ItemStatus.ON_SALE)
                    .category(ItemCategory.DESSERT)
                    .quantity(5)
                    .build();

            Item item5 = Item.builder()
                    .name("감자빵")
                    .price(2500)
                    .description("강원도 감자로 만든 쫄깃한 감자빵")
                    .status(ItemStatus.ON_SALE)
                    .category(DESSERT)
                    .quantity(5)
                    .build();

            Item item6 = Item.builder()
                    .name("스타벅스 텀블러")
                    .price(30000)
                    .description("스타벅스 한정판 텀블러")
                    .status(ItemStatus.ON_SALE)
                    .category(ETC)
                    .quantity(1)
                    .build();

            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
            itemRepository.save(item4);
            itemRepository.save(item5);
            itemRepository.save(item6);

            System.out.println("초기 상품 데이터가 성공적으로 삽입되었습니다.");
        } else {
            System.out.println("초기 데이터가 이미 존재합니다. 삽입을 건너뜁니다.");
        }
    }
}
