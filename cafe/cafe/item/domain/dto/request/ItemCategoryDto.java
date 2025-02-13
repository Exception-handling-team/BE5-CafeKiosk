package programmers.cafe.item.domain.dto.request;

import lombok.Data;
import programmers.cafe.item.domain.entity.ItemCategory;

@Data
public class ItemCategoryDto {
    private ItemCategory category;
}
