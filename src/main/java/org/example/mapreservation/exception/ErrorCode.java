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

    // CSRF
    CF_ERROR("유효하지 않은 CSRF 토큰입니다.", HttpStatus.FORBIDDEN),

    // OWNER
    OWNER_NOT_FOUND("일치하는 고객이 없습니다.", HttpStatus.NOT_FOUND),


    // Hair Shop
    HS_NOT_FOUND("찾을 수 없는 헤어샵입니다.", HttpStatus.NOT_FOUND),

    // Hair Shop Reservation
    HSR_OLD_RESERVATION_TIME("이미 지나간 시간으로 헤어샵을 예약할 수 없습니다.", HttpStatus.BAD_REQUEST),
    HSR_INVALID_RESERVATION_TIME("예약 시간은 0분 또는 30분이어야 합니다.", HttpStatus.BAD_REQUEST),
    HSR_ALREADY_TAKEN_RESERVATION_TIME("이미 예약된 시간입니다.", HttpStatus.CONFLICT),
    HSR_NOT_FOUND("일치하는 예약이 없습니다.", HttpStatus.NOT_FOUND),
    HSR_OLD_RESERVATION_TIME_CANNOT_CANCEL("이미 지나간 예약이므로 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Lock
    LCK_CANNOT_ACQUIRE_LOCK("일시적인 오류가 발생했습니다.", HttpStatus.LOCKED),

    // Common
    CMM_FIELD_VALIDATION_FAILURE("필드 값 검증에 실패했습니다.", null);

    /**
     * 에러 코드 생성자
     *
     * @param message    에러 메시지
     * @param httpStatus http 상태
     */
    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final HttpStatus httpStatus;

}
