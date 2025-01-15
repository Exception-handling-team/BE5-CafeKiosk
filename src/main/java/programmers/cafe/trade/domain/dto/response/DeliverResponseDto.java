package programmers.cafe.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import programmers.cafe.trade.domain.entity.TradeStatus;

@Data
@AllArgsConstructor
public class DeliverResponseDto {
    private String tradeUUID;
    private TradeStatus status;
}
