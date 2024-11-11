package org.example.mapreservation.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.auth.dto.LoginSuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 고객 로그인 성공 시 커스텀 응답 생성
 */
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    /**
     * 로그인 성공 시, SessionId 가 변경되고(기본 sessionFixation strategy = changeSessionId)
     * CsrfAuthenticationStrategy 에 의해 새로운 CSRF 토큰이 생성되어 기존 것을 대체한다. (기본 sessionAuthenticationStrategy = CsrfAuthenticationStrategy)
     * 그러므로, 프론트엔드 애플리케이션은 로그인 후 CSRF 토큰을 재 발급해야 한다.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        LoginSuccessResponse dto = LoginSuccessResponse.from(user);
        response.setStatus(HttpStatus.CREATED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), dto);
    }
}
