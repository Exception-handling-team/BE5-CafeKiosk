package programmers.cafe.item.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programmers.cafe.global.wrapper.response.ApiResponse;
import programmers.cafe.item.domain.dto.request.ItemCategoryDto;
import programmers.cafe.item.domain.dto.request.ItemModifyInfoDto;
import programmers.cafe.item.domain.dto.request.ItemRegisterDto;
import programmers.cafe.item.domain.dto.response.ItemResponseDto;
import programmers.cafe.item.domain.dto.response.ItemSimpleResponseDto;
import programmers.cafe.item.domain.dto.response.ShowSimpleItem;
import programmers.cafe.item.service.ItemService;

import java.util.List;

@Tag(name = "상품 관리", description = "상품 등록, 상품 정보 수정, 상품 상태 변경, 상품 삭제")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ItemAdminController {
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
    public ResponseEntity<ApiResponse<ItemResponseDto>> itemRegister(@RequestBody ItemRegisterDto itemDto) {
        return ResponseEntity.ok(new ApiResponse<>("new item register success",service.register(itemDto)));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShowSimpleItem>>> showAllItem() {
        return ResponseEntity.ok(new ApiResponse<>("show all items",service.showAllItems()));
    }

    @PostMapping("/items/cat")
    public ResponseEntity<ApiResponse<List<ShowSimpleItem>>> showCategoryItem(@RequestBody ItemCategoryDto categoryDto) {
        return ResponseEntity.ok(new ApiResponse<>("show category items", service.showCategoryItem(categoryDto.getCategory())));
    }

    @GetMapping("/item")
    public ResponseEntity<ApiResponse<ShowSimpleItem>> showSingleItem(@RequestParam("id") Long id) {
        return ResponseEntity.ok(new ApiResponse<>("show item by id", service.showSingleItem(id)));
    }

    @PutMapping("/edit/info")
    public ResponseEntity<ApiResponse<ItemResponseDto>> editInfo(@RequestParam("id") Long id, @RequestBody ItemModifyInfoDto itemDto) {
        return ResponseEntity.ok(new ApiResponse<>("show item info",service.modifyInfo(id, itemDto)));
    }

    @PutMapping("/edit/status")
    public ResponseEntity<ApiResponse<ItemSimpleResponseDto>> editStatus(@RequestParam("id") Long id) {
        return ResponseEntity.ok(new ApiResponse<>("edit item success", service.modifyStatus(id)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Long>> deleteItem(@RequestParam Long id) {
        return ResponseEntity.ok(new ApiResponse<>("delete item success", service.removeItem(id)));
    }
}
