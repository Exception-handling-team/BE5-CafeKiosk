package programmers.cafe.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayRequestDto {
    private String tradeUUID;
    private Integer payment;
}
