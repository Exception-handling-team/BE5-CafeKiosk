package programmers.cafe.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import programmers.cafe.trade.domain.entity.TradeStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResponseDto {
    private Long tradeId;
    private String tradeUUID;
    private TradeStatus status;
    private Integer totalPrice;
}
