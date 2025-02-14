package programmers.cafe.member.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import programmers.cafe.member.domain.entity.Users;
import programmers.cafe.member.jwt.domain.dto.JoinRequest;
import programmers.cafe.member.jwt.domain.dto.LoginRequest;
import programmers.cafe.member.jwt.domain.dto.TokenDto;
import programmers.cafe.member.jwt.domain.dto.TokenRequestDto;
import programmers.cafe.member.jwt.domain.dto.UserResponseDto;
import programmers.cafe.member.jwt.domain.entity.RefreshToken;
import programmers.cafe.member.jwt.jwt.TokenProvider;
import programmers.cafe.member.jwt.repository.RefreshTokenRepository;
import programmers.cafe.member.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${admin_signup_secretKey}")
    private String key;

    @Transactional
    public UserResponseDto signup(JoinRequest joinRequest) {
        if (!joinRequest.getAdminKey().equals(key)) {
            throw new RuntimeException("관리자 인증에 실패하였습니다.");
        }

        if (userRepository.existsByLoginId(joinRequest.getLoginId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Users user = joinRequest.toEntity(passwordEncoder.encode(joinRequest.getPassword()));
        return UserResponseDto.of(userRepository.save(user));
    }

    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        // 1. Login ID/PW 기반 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

        // 2. 사용자 인증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. Refresh Token 저장 (키: 사용자 ID, 값: refresh token)
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        // 5. TokenDto 반환 (컨트롤러에서 access token만 응답, refresh token은 쿠키에 저장)
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token에서 사용자 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 사용자에 해당하는 Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치 여부 확인
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto;
        if (tokenProvider.refreshTokenPeriodCheck(refreshToken.getValue())) {
            // Refresh Token 유효기간이 3일 미만일 경우, access와 refresh 모두 재발급
            tokenDto = tokenProvider.generateTokenDto(authentication);
            RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
            refreshTokenRepository.save(newRefreshToken);
        } else {
            // Refresh Token 유효기간이 3일 이상일 경우, access token만 재발급
            tokenDto = tokenProvider.createAccessToken(authentication);
        }
        return tokenDto;
    }
}


//package programmers.cafe.member.jwt.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import programmers.cafe.member.domain.entity.Users;
//import programmers.cafe.member.jwt.domain.dto.*;
//import programmers.cafe.member.jwt.domain.entity.RefreshToken;
//import programmers.cafe.member.jwt.jwt.TokenProvider;
//import programmers.cafe.member.jwt.repository.RefreshTokenRepository;
//import programmers.cafe.member.repository.UserRepository;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final TokenProvider tokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    @Transactional
//    public UserResponseDto signup(JoinRequest joinRequest) {
//        if (userRepository.existsByLoginId(joinRequest.getLoginId())) {
//            throw new RuntimeException("이미 가입되어 있는 유저입니다");
//        }
//
//        Users user = joinRequest.toEntity(passwordEncoder.encode(joinRequest.getPassword()));
//        return UserResponseDto.of(userRepository.save(user));
//    }
//
//    @Transactional
//    public TokenDto login(LoginRequest loginRequest) {
//        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
//        log.info("1. Login ID/PW 를 기반으로 AuthenticationToken 생성");
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());
//
//        log.info("{}", authenticationToken);
//        log.info("{}, {}", loginRequest.getLoginId(), loginRequest.getPassword());
//
//
//
//
//        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
//        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
//        log.info("2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분");
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//
//        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        log.info("3. 인증 정보를 기반으로 JWT 토큰 생성");
//        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//
//        // 4. RefreshToken 저장
//        log.info("4. RefreshToken 저장");
//        RefreshToken refreshToken = RefreshToken.builder()
//                .key(authentication.getName())
//                .value(tokenDto.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);
//
//        // 5. 토큰 발급
//        log.info("5. 토큰 발급");
//        return tokenDto;
//    }
//
//    @Transactional
//    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
//        // 1. Refresh Token 검증
//        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
//        }
//
//        // 2. Access Token 에서 Member ID 가져오기
//        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//
//        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
//        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));
//
//        // 4. Refresh Token 일치하는지 검사
//        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
//        }
//
//        // 5. 새로운 토큰 생성
//        TokenDto tokenDto = null;
//        if (tokenProvider.refreshTokenPeriodCheck(refreshToken.getValue())) {
//            // 5-1. Refresh Token의 유효기간이 3일 미만일 경우 전체(Access / Refresh) 재발급
//            tokenDto = tokenProvider.generateTokenDto(authentication);
//
//            // 6. Refresh Token 저장소 정보 업데이트
//            RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
//            refreshTokenRepository.save(newRefreshToken);
//        } else {
//            // 5-2. Refresh Token의 유효기간이 3일 이상일 경우 Access Token만 재발급
//            tokenDto = tokenProvider.createAccessToken(authentication);
//        }
//
//        // 토큰 발급
//        return tokenDto;
//    }
//}