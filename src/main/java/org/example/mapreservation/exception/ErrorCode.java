package org.example.mapreservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Customer
    CUST_ALREADY_TAKEN_EMAIL("이미 가입된 이메일 주소입니다.", HttpStatus.BAD_REQUEST);

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final HttpStatus httpStatus;

}
