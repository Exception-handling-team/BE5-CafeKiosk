package programmers.cafe.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 프론트엔드 주소를 정확히 명시 (와일드카드(*) 불가)
                .allowedOrigins("http://localhost:3000")
                // 요청 메서드 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 요청 헤더 허용 (필요 시 '*' 또는 구체적 헤더 목록)
                .allowedHeaders("*")
                // 인증 정보(쿠키) 전송 허용
                .allowCredentials(true)
                // (옵션) preflight 요청 캐시 시간(초)
                .maxAge(3600);
    }
}

