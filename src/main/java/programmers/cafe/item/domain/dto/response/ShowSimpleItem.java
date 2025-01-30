package programmers.cafe.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import programmers.cafe.item.domain.entity.ItemStatus;

@Getter
@AllArgsConstructor
public class ShowSimpleItem {
    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
    private ItemStatus status;
}
