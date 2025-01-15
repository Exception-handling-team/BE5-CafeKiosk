package programmers.cafe.trade.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "trade")
public class Trade{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    @Setter
    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL)
    private List<TradeItem> tradeItems; // 거래에 포함된 아이템 목록

    @Setter
    private Integer totalPrice;


    @Column(name = "tradeUUID")
    private String tradeUUid;

    private LocalDateTime tradeTime;
}
