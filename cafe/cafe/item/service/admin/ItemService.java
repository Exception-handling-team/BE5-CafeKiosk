package programmers.cafe.item.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.exception.ItemNotFoundException;
import programmers.cafe.item.domain.dto.request.ItemModifyInfoDto;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.dto.response.ItemResponseDto;
import programmers.cafe.item.domain.dto.response.ItemSimpleResponseDto;
import programmers.cafe.item.domain.dto.response.ShowSimpleItem;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemCategory;
import programmers.cafe.item.repository.ItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    /**
     * register 상품 등록
     * showAllItem
     * showSingleItem
     * showCategoryItem
     * modifyInfo 상품 수정 -> 이름, 가격, 수량, 상세설명 / 상태 제외
     * modifyStatus 상품 수정 -> 상태만 변경. 호출 시 스위치 형식으로 변경됨. 판매중 <-> 품절
     * remove 상품 삭제
     */



    @Transactional
    public ItemResponseDto register(ItemRegisterDto itemDto) {
        Item item = Item.builder()
                .name(itemDto.getName())
                .price(itemDto.getPrice())
                .description(itemDto.getDescription())
                .status(itemDto.getStatus())
                .category(itemDto.getCategory())
                .quantity(itemDto.getQuantity())
                .build();


        itemRepository.save(item);
        return new ItemResponseDto(item.getId(), item.getName(), item.getPrice(), item.getQuantity(), item.getDescription());
    }

    @Transactional
    public ItemResponseDto modifyInfo(Long id,ItemModifyInfoDto itemDto) {
        Item modifyItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("수정하고자 하는 상품을 찾을 수 없음"));

        setIfNotNull(modifyItem::setName, itemDto.getName());
        setIfNotNull(modifyItem::setPrice, itemDto.getPrice());
        setIfNotNull(modifyItem::setQuantity, itemDto.getQuantity());
        setIfNotNull(modifyItem::setDescription, itemDto.getDescription());

        return new ItemResponseDto(modifyItem.getId(), modifyItem.getName(), modifyItem.getPrice(), modifyItem.getQuantity(), itemDto.getDescription());
    }

    @Transactional
    public ItemSimpleResponseDto modifyStatus(Long id) {
        Item modifiyItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("수정하고자 하는 상품을 찾을 수 없음"));

        modifiyItem.setStatus();

        return new ItemSimpleResponseDto(modifiyItem.getId(), modifiyItem.getName(), modifiyItem.getStatus());
    }

    @Transactional
    public Long removeItem(Long id) {
        Item removeItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("삭제하고자 하는 상품을 찾을 수 없음"));

        itemRepository.delete(removeItem);
        return removeItem.getId();
    }

    @Transactional(readOnly = true)
    public List<ShowSimpleItem> showAllItems() {
        return itemRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowSimpleItem showSingleItem(Long id) {
        Item findItem = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없음"));
        return convertToDto(findItem);
    }

    @Transactional(readOnly = true)
    public List<ShowSimpleItem> showCategoryItem(ItemCategory category) {
        return itemRepository.findByCategory(category).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void clearAllItems() {
        itemRepository.deleteAll();
    }

    private <T> void setIfNotNull(Consumer<T> setter, T value) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    private ShowSimpleItem convertToDto(Item item) {
        return new ShowSimpleItem(item.getId(), item.getName(), item.getPrice());
    }
}
