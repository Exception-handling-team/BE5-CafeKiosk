package programmers.cafe.item.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programmers.cafe.item.domain.dto.request.ItemCategoryDto;
import programmers.cafe.item.domain.dto.request.ItemModifyInfoDto;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.dto.response.ItemResponseDto;
import programmers.cafe.item.domain.dto.response.ItemSimpleResponseDto;
import programmers.cafe.item.domain.dto.response.ShowSimpleItem;
import programmers.cafe.item.service.admin.ItemService;

import java.util.List;

@Tag(name = "상품 관리", description = "상품 등록, 상품 정보 수정, 상품 상태 변경, 상품 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ItemController {
    private final ItemService service;

    /**
     * service           : url endpoint        :설명
     * register          : /register           :상품 등록
     * showAllItem       : /items              :모든 상품 리스트
     * showSingleItem    : /item?id=1          :상품 단건 검색
     * showCategoryItem  : /items/cat          :카테고리 별 상품 검색
     * modifyInfo        : /edit/info?id=1     :상품 수정 (상태 제외)
     * modifyStatus      : /edit/status?id=1   :상품 수정 (상태만 변경)
     * remove            : /delete?id=1        :상품 삭제
     */

    @PostMapping("/register")
    public ResponseEntity<ItemResponseDto> itemRegister(ItemRegisterDto itemDto) {
        return ResponseEntity.ok(service.register(itemDto));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ShowSimpleItem>> showAllItem() {
        return ResponseEntity.ok(service.showAllItems());
    }

    @GetMapping("/items/cat")
    public ResponseEntity<List<ShowSimpleItem>> showCategoryItem(ItemCategoryDto categoryDto) {
        return ResponseEntity.ok(service.showCategoryItem(categoryDto.getCategory()));
    }

    @GetMapping("/item")
    public ResponseEntity<ShowSimpleItem> showSingleItem(@RequestParam("id") Long id) {
        return ResponseEntity.ok(service.showSingleItem(id));
    }

    @PutMapping("/edit/info")
    public ResponseEntity<ItemResponseDto> editInfo(@RequestParam("id") Long id, ItemModifyInfoDto itemDto) {
        return ResponseEntity.ok(service.modifyInfo(id, itemDto));
    }

    @PutMapping("/edit/status")
    public ResponseEntity<ItemSimpleResponseDto> editStatus(@RequestParam("id") Long id) {
        return ResponseEntity.ok(service.modifyStatus(id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Long> deleteItem(@RequestParam Long id) {
        return ResponseEntity.ok(service.removeItem(id));
    }
}
