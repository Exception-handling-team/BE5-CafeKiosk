package programmers.cafe.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CancelResponseDto {
    private String tradeUUID;
    private String message;
}
