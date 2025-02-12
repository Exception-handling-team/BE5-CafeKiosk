package programmers.cafe.member.jwt.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import programmers.cafe.member.jwt.domain.dto.TokenDto;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {
    private static final String authorities_key = "auth";
    private static final String bearer_type = "Bearer";
    private static final long access_token_expire_time = 1000 * 60 * 30;
    private static final long refresh_token_expire_time = 1000 * 60 * 60 * 24 * 7;
    private static final long three_days = 1000 * 60 * 60 * 24 * 3;

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 만 만들어서 return.
     * @param authentication
     * @return tokenDto
     */
    public TokenDto createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        //access token 생성
        Date accessTokenExpiresIn = new Date(now + access_token_expire_time);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(authorities_key, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(bearer_type)
                .accessToken(accessToken)
                .accessTokenExpireIn(accessTokenExpiresIn.getTime())
                .refreshToken(null)
                .build();
    }


    /**
     * access token, refresh token 둘 다 생성 후 반환
     *
     * @param authentication
     * @return tokenDto
     */
    public TokenDto generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + access_token_expire_time);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // payload "sub": "name"
                .claim(authorities_key, authorities)        // payload "auth": "USER"
                .setExpiration(accessTokenExpiresIn)        // payload "exp": 151621022 (ex)
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refresh_token_expire_time))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(bearer_type)
                .accessToken(accessToken)
                .accessTokenExpireIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // access 토큰을 통해 권한 확인.
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(authorities_key) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(authorities_key).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean refreshTokenPeriodCheck(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        long now = (new Date()).getTime();
        long refresh_expiredTime = claimsJws.getBody().getExpiration().getTime();
        long refresh_nowTime = new Date(now + refresh_token_expire_time).getTime();

        if (refresh_nowTime - refresh_expiredTime > three_days) {
            return true;
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
