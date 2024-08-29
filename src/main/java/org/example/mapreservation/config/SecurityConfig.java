package org.example.mapreservation.config;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.auth.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // SpringDocConfig의 Bean으로부터 받아오는 값
    private final String swaggerPath;
    private final String apiDocPath;

    /**
     * HttpSession에 CsrfToken을 저장하는 CsrfTokenRepository 리턴
     * HttpSessionTokenRepository를 선택하여 세션과 CsrfToken이 연관되도록 하였다.
     * <a href="https://binchoo.tistory.com/46">자세한 사유는 이 글을 참조</a>
     *
     * @return 사용할 HttpSessionCsrfTokenRepository
     */
    private HttpSessionCsrfTokenRepository sessionCsrfTokenRepository() {
        HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
        csrfTokenRepository.setHeaderName(CsrfConst.headerName);
        csrfTokenRepository.setParameterName(CsrfConst.parameterName);
        csrfTokenRepository.setSessionAttributeName(CsrfConst.sessionAttributeName);
        return csrfTokenRepository;
    }

    /**
     * 브라우저의 preflight 요청의 Access-Control-Request-* 에 대한 Access-Control-Allow-* 응답 설정 객체 리턴
     *
     * @return cors 응답 설정 객체
     */
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = List.of("http://localhost:3000");
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

        // 설정이 적용될 Url 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(swaggerPath).permitAll()
                        .requestMatchers(apiDocPath).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/csrf-token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hairshop").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hairshops/{hairShopId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hairshops/{hairShopId}/reservations/status").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(
                        customizer -> customizer.csrfTokenRepository(sessionCsrfTokenRepository())
                )
                .formLogin(customizer -> customizer
                        // UsernamePasswordAuthenticationFilter에서 로그인 진행
                        .loginProcessingUrl("/api/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(customizer -> customizer
                        // LogoutFilter에서 로그아웃 진행
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
