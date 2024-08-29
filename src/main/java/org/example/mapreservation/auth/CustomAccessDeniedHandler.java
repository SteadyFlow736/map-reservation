package org.example.mapreservation.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.exception.CustomErrorResponse;
import org.example.mapreservation.exception.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CSRF 인증 실패 또는 권한 부족 요청에 대한 응답 생성
 */
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // TODO: CsrfException의 detailedMessage(예: Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'.)를 응답에 포함시키기
        if (accessDeniedException instanceof CsrfException) {
            ErrorCode errorCode = ErrorCode.CF_ERROR;
            response.setStatus(errorCode.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8"); // 한글 응답이 포함되어 있으므로 UTF-8로 인코딩

            CustomErrorResponse<Object> errorResponse = CustomErrorResponse.from(errorCode, null);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }

        //TODO: 권한 부족 시 응답 추가
    }
}
