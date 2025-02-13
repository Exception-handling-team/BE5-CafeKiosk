package programmers.cafe.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import programmers.cafe.trade.domain.entity.TradeStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long tradeId;
    private TradeStatus status;
    private Integer totalPrice;
    private String tradeUUID;
}
