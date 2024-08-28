package org.example.mapreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ExceptionHandler 호출 우선순위
 * 1. 가장 구체적인 예외 유형의 핸들러
 * 2. 더 먼저(더 위에) 정의된 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 컨트롤러의 bean validation 적용된 ModelAttribute, RequestBody 객체 검증 실패 핸들러
     * 타입 변환 실패, 검증 실패 모두 다룬다.
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleException(MethodArgumentNotValidException ex) {
        // TODO
        return null;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(CustomException ex) {
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(DataIntegrityViolationException exception) {
        CustomException ex = new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode, null);
    }

    private <T> ResponseEntity<CustomErrorResponse<T>> createResponseEntity(ErrorCode errorCode, T errors) {
        CustomErrorResponse<T> customErrorResponse = CustomErrorResponse.from(errorCode, errors);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(customErrorResponse);
    }

    private void logError(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
    }

}
