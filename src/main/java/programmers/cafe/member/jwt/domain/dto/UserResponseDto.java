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
public class UserResponseDto {
    private Long id;
    private String loginId;

    public static UserResponseDto of(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .build();
    }
}