package programmers.cafe.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import programmers.cafe.item.domain.entity.ItemStatus;

@Data
@AllArgsConstructor
public class ItemSimpleResponseDto {
    private Long itemId;
    private String name;
    private ItemStatus status;
}
