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
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
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
