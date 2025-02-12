package programmers.cafe.trade.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CancelInfoDto {
    private String UUID;
    private Long tradeId;
    private List<CancelItemInfo> itemList;
    private Integer totalPrice;

    @Data
    @AllArgsConstructor
    public static class CancelItemInfo {
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private Integer price;
    }
}
