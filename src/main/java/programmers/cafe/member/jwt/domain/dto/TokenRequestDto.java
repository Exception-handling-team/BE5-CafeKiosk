package programmers.cafe.member.jwt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}
