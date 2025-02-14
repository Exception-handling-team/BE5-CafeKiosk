package programmers.cafe.member.jwt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import programmers.cafe.member.domain.entity.Users;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequest {
    private String loginId;
    private String password;
    private String adminKey;

    public Users toEntity(String encodedPassword) {
        return Users.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .build();
    }
}