package programmers.cafe.item.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.geom.RoundRectangle2D;

@Getter
@AllArgsConstructor
public class ItemModifyInfoDto {
    //    이름, 가격, 수량, 상세설명 / 상태 제외
    private String name;
    private Integer price;
    private Integer quantity;
    private String description;

}
