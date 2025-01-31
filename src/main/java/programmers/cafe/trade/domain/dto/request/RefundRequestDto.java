package programmers.cafe.trade.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RefundRequestDto {
    private String tradeUUID;
}
