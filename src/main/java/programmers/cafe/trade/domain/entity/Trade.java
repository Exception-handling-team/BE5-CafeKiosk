package programmers.cafe.trade.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "trade")
@EntityListeners(AuditingEntityListener.class)
public class Trade{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    @Setter
    @OneToMany(mappedBy = "trade")
    private List<TradeItem> tradeItems; // 거래에 포함된 아이템 목록

    @Setter
    private Integer totalPrice;


    @Column(name = "tradeUUID")
    @Setter
    private String tradeUUid;

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime tradeRequestDate;

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime tradeUpdatedDate;
}
