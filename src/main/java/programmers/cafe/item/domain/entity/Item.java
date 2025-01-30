package programmers.cafe.item.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import programmers.cafe.global.entity.BaseEntity;

import java.time.LocalDateTime;

import static programmers.cafe.item.domain.entity.ItemStatus.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item")
@EntityListeners(AuditingEntityListener.class)
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Setter
    private String name;

    @Setter
    private Integer price;

    @Setter
    private String description;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Setter
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Setter
    private Integer quantity;


    public void autoCheckQuantityForSetStatus() {
        if (this.getQuantity() <= 0) {
            this.status = SOLD_OUT;
        } else {
            this.status = ON_SALE;
        }
    }

    public void setStatus() {
        if (status.equals(ON_SALE)) {
            this.status = SOLD_OUT;
        } else {
            this.status = ON_SALE;
        }
    }

    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime modifiedDate;
}
