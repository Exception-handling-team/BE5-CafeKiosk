package programmers.cafe.trade.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundRequestDto {
    private final String tradeUUID;

    @JsonCreator
    public RefundRequestDto(@JsonProperty("tradeUUID") String tradeUUID) {
        this.tradeUUID = tradeUUID;
    }
//    private String tradeUUID;
}
