package programmers.cafe.member.jwt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programmers.cafe.global.wrapper.response.ApiResponse;
import programmers.cafe.member.jwt.domain.dto.*;
import programmers.cafe.member.jwt.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(@RequestBody JoinRequest joinRequest) {
        return ResponseEntity.ok(new ApiResponse<>("회원가입에 성공하였습니다.", authService.signup(joinRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@RequestBody LoginRequest loginRequest,
                                                                  HttpServletResponse response) {
        TokenDto tokenDto = authService.login(loginRequest);

        // refresh token을 HttpOnly, Secure 쿠키에 저장
        Cookie refreshCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 운영 환경에서는 true, 개발 환경에서는 상황에 맞게 조정
        refreshCookie.setPath("/");
        // 예시: refresh token의 유효기간을 7일로 설정 (초 단위)
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        String cookieValue = String.format(
                "refreshToken=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=Strict",
                tokenDto.getRefreshToken(),
                7 * 24 * 60 * 60
        );
        response.setHeader("Set-Cookie", cookieValue);


        // access token만 응답 데이터로 전달
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(tokenDto.getAccessToken());
        return ResponseEntity.ok(new ApiResponse<>("로그인에 성공하였습니다.", accessTokenResponse));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> reissue(@RequestBody TokenRequestDto tokenRequestDto,
                                                                    HttpServletResponse response) {
        TokenDto tokenDto = authService.reissue(tokenRequestDto);

        // 새로 발급된 refresh token이 있다면 쿠키 업데이트
        if (tokenDto.getRefreshToken() != null) {
            Cookie refreshCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());

            String cookieValue = String.format(
                    "refreshToken=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=Strict",
                    tokenDto.getRefreshToken(),
                    7 * 24 * 60 * 60
            );
            response.setHeader("Set-Cookie", cookieValue);


            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(refreshCookie);
        }

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(tokenDto.getAccessToken());
        return ResponseEntity.ok(new ApiResponse<>("토큰 발급에 성공하였습니다.", accessTokenResponse));
    }
}


//package programmers.cafe.member.jwt.controller;
//
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import jakarta.validation.constraints.NotBlank;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import programmers.cafe.global.wrapper.response.ApiResponse;
//import programmers.cafe.member.jwt.domain.dto.*;
//import programmers.cafe.member.jwt.service.AuthService;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthService authService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<UserResponseDto>> signup(@RequestBody JoinRequest joinRequest) {
//        return ResponseEntity.ok(new ApiResponse<>("회원가입에 성공하였습니다.", authService.signup(joinRequest)));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest loginRequest) {
//        return ResponseEntity.ok(new ApiResponse<>("로그인에 성공하였습니다.", authService.login(loginRequest)));
//    }
//
//    @PostMapping("/reissue")
//    public ResponseEntity<ApiResponse<TokenDto>> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
//        return ResponseEntity.ok(new ApiResponse<>("토큰 발급에 성공하였습니다.", authService.reissue(tokenRequestDto)));
//    }
//}