package programmers.cafe.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // OPTIONS 요청 허용을 위해 import
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import programmers.cafe.member.jwt.jwt.JwtTokenFilter;
import programmers.cafe.member.jwt.jwt.TokenProvider;
import programmers.cafe.member.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. 세션 정책, CSRF, HTTP Basic 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 2. CORS 설정
        //    - allowedOrigins에 "http://localhost:3000"과 같이 구체적인 Origin을 지정
        //    - withCredentials(true)를 사용 중이므로 allowCredentials(true) 필수
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 3. JWT 필터 등록 (UsernamePasswordAuthenticationFilter 이전에)
        http.addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        // 4. 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                // (선택) OPTIONS 프리플라이트 요청은 인증 없이 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/*").authenticated()
                .anyRequest().permitAll()
        );

        // 5. 예외처리
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

        // (선택) H2 콘솔 사용 시 frameOptions 비활성화
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    // 별도 CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 요청을 허용할 Origin (프론트엔드 주소)
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        // 허용할 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 헤더
        config.setAllowedHeaders(List.of("*"));
        // 인증 정보(쿠키) 전송 허용
        config.setAllowCredentials(true);
        // 필요하다면 아래처럼 구체적인 만료시간, 노출 헤더 등 설정 가능
        // config.setMaxAge(3600L);
        // config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 엔드포인트(**)에 대해 위 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 가장 흔히 쓰는 BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }
}


//package programmers.cafe.global.config;
//
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import programmers.cafe.member.jwt.jwt.JwtTokenFilter;
//import programmers.cafe.member.jwt.jwt.TokenProvider;
//import programmers.cafe.member.service.CustomUserDetailsService;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//
//
//    private final TokenProvider tokenProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // CSRF, HTTP Basic 비활성화 및 세션 정책 설정 (STATELESS)
//        http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                );
//
//        // JwtTokenFilter를 UsernamePasswordAuthenticationFilter 전에 추가
//        http.addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
//
//
//        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/h2-console/**")
//                .permitAll()
//                .requestMatchers("/auth/**")
//                .permitAll()
//                .requestMatchers("/admin/*").authenticated()
//                .anyRequest().permitAll()
//        );
//
//        // 예외처리 설정
//        http.exceptionHandling(exceptionHandling -> exceptionHandling
//                .authenticationEntryPoint((request, response, authException) ->
//                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized"))
//                .accessDeniedHandler((request, response, accessDeniedException) ->
//                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
//        );
//
//        // frameOptions 비활성화
//        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // 가장 흔히 쓰는 BCryptPasswordEncoder
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       CustomUserDetailsService customUserDetailsService,
//                                                       PasswordEncoder passwordEncoder) throws Exception {
//        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authBuilder.userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder);
//        return authBuilder.build();
//    }
//}
//
