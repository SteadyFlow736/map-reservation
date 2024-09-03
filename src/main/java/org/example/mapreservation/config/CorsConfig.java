package org.example.mapreservation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties("cors")
@Configuration
public class CorsConfig {

    private List<String> allowedOrigins;

    /**
     * 브라우저의 preflight 요청의 Access-Control-Request-* 에 대한 Access-Control-Allow-* 응답 설정 객체 리턴
     *
     * @return cors 응답 설정 객체
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = getCorsConfiguration();

        // 설정이 적용될 Url 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private CorsConfiguration getCorsConfiguration() {
        List<String> allowedMethods = List.of("*");
        List<String> allowedHeaders = List.of("*");

        CorsConfiguration configuration = new CorsConfiguration();
        // preflight 요청에 대한 응답 헤더의 Access-Control-Allow-Origin 값 설정
        configuration.setAllowedOrigins(allowedOrigins);
        // preflight 요청에 대한 응답 헤더의 Access-Control-Allow-Credentials 값을 true 로 설정
        configuration.setAllowCredentials(true);
        // preflight 요청에 대한 응답 헤더의 Access-Control-Allow-Methods 값 설정
        configuration.setAllowedMethods(allowedMethods);
        // preflight 요청에 대한 응답 헤더의 Access-Control-Allow-Headers 값 설졍
        configuration.setAllowedHeaders(allowedHeaders);
        return configuration;
    }
}
