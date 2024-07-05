package org.example.mapreservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Customer
    CUST_ALREADY_TAKEN_EMAIL("이미 가입된 이메일 주소입니다.", HttpStatus.BAD_REQUEST),
    CUST_NOT_FOUND("일치하는 고객이 없습니다.", HttpStatus.NOT_FOUND),

    // AUTH
    AUTH_INVALID_USERNAME_OR_PASSWORD("존재하지 않는 사용자거나 패스워드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    AUTH_NEED_AUTHENTICATION("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // Hair Shop
    HS_NOT_FOUND("찾을 수 없는 헤어샵입니다.", HttpStatus.NOT_FOUND),

    // Hair Shop Reservation
    HSR_OLD_RESERVATION_TIME("이미 지나간 시간으로 헤어샵을 예약할 수 없습니다.", HttpStatus.BAD_REQUEST),
    HSR_INVALID_RESERVATION_TIME("예약 시간은 0분 또는 30분이어야 합니다.", HttpStatus.BAD_REQUEST),
    HSR_ALREADY_TAKEN_RESERVATION_TIME("이미 예약된 시간입니다.", HttpStatus.CONFLICT),
    ;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final HttpStatus httpStatus;

}
