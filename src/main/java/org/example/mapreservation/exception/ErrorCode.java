package org.example.mapreservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Customer
    CUST_ALREADY_TAKEN_EMAIL("이미 가입된 이메일 주소입니다.", HttpStatus.BAD_REQUEST),

    // AUTH
    AUTH_INVALID_USERNAME_OR_PASSWORD("존재하지 않는 사용자거나 패스워드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    AUTH_NEED_AUTHENTICATION("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final HttpStatus httpStatus;

}
