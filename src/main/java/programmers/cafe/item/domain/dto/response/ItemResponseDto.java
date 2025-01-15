package programmers.cafe.item.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import programmers.cafe.item.domain.entity.ItemCategory;
import programmers.cafe.item.domain.entity.ItemStatus;


@Getter
@AllArgsConstructor
@NotBlank
public class ItemResponseDto {
    private Long itemId;
    private String name;
    private Integer price;
    private Integer quantity;
    private String description;
}
