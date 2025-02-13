package programmers.cafe.item.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import programmers.cafe.item.domain.entity.ItemCategory;
import programmers.cafe.item.domain.entity.ItemStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NotBlank
public class ItemRegisterDto {
    private String name;
    private Integer price;
    private String description;
    private ItemStatus status;
    private ItemCategory category;
    private Integer quantity;
}
