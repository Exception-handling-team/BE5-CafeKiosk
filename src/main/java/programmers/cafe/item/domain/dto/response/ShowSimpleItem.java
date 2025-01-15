package programmers.cafe.item.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowSimpleItem {
    private Long id;
    private String name;
    private Integer price;
}
