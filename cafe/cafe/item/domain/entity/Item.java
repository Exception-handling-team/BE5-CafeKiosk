package programmers.cafe.item.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import programmers.cafe.global.entity.BaseEntity;

import static programmers.cafe.item.domain.entity.ItemStatus.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public void setStatus() {
        if (status.equals(ON_SALE)) {
            this.status = SOLD_OUT;
        } else {
            this.status = ON_SALE;
        }
    }
}
