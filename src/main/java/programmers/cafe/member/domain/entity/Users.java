package programmers.cafe.member.domain.entity;


import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    // 추후 필요시 email, 이름 등의 필드를 추가할 수 있습니다.
}
