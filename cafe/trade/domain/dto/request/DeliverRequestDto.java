package programmers.cafe.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliverRequestDto {
    private String tradeUUID;
}
