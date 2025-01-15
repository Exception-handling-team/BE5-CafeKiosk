package programmers.cafe.global.entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {
    private LocalDateTime time;

    public void setTime() {
        this.time = LocalDateTime.now();
    }
}
