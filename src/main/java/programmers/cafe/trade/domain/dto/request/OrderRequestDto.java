package programmers.cafe.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequestDto {
    private Long itemId;
    private Integer quantity;
}
