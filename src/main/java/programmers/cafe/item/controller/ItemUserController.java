package programmers.cafe.item.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programmers.cafe.global.wrapper.response.ApiResponse;
import programmers.cafe.item.domain.dto.request.ItemCategoryDto;
import programmers.cafe.item.domain.dto.response.ShowSimpleItem;
import programmers.cafe.item.service.ItemService;

import java.util.List;

@Tag(name = "사용자용 메뉴판", description = "전체 상품 조회, 단건 조회, 카테고리별 조회")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping("/user")
public class ItemUserController {
    private final ItemService service;

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShowSimpleItem>>> showAllItem() {
        return ResponseEntity.ok(new ApiResponse<>("show all items",service.showAllItems()));
    }

    @GetMapping("/items/cat")
    public ResponseEntity<ApiResponse<List<ShowSimpleItem>>> showCategoryItem(@RequestBody ItemCategoryDto categoryDto) {
        return ResponseEntity.ok(new ApiResponse<>("show category items", service.showCategoryItem(categoryDto.getCategory())));
    }

    @GetMapping("/item")
    public ResponseEntity<ApiResponse<ShowSimpleItem>> showSingleItem(@RequestParam("id") Long id) {
        return ResponseEntity.ok(new ApiResponse<>("show item by id", service.showSingleItem(id)));
    }
}
