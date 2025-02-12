package programmers.cafe.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import programmers.cafe.member.jwt.jwt.JwtTokenFilter;
import programmers.cafe.member.jwt.jwt.TokenProvider;
import programmers.cafe.member.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {



    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF, HTTP Basic 비활성화 및 세션 정책 설정 (STATELESS)
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // JwtTokenFilter를 UsernamePasswordAuthenticationFilter 전에 추가
        http.addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        // 인가 설정: /question/* 요청은 인증 필요, 그 외는 모두 허용
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/*").authenticated()
                .anyRequest().permitAll()
        );

        // 예외처리 설정
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

        // frameOptions 비활성화
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 가장 흔히 쓰는 BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       CustomUserDetailsService customUserDetailsService,
                                                       PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }
}

