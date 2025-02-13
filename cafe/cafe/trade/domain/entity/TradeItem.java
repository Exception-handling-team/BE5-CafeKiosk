package programmers.cafe.trade.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import programmers.cafe.item.domain.entity.Item;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // Item 삭제 시 TradeItem도 삭제
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantity; // 해당 아이템의 수량
}