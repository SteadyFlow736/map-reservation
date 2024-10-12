package org.example.mapreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 컨트롤러의 bean validation 적용된 ModelAttribute, RequestBody 객체 검증 실패 핸들러
     * 타입 변환 실패, 검증 실패 모두 다룬다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleException(MethodArgumentNotValidException ex) {
        logError(ex);
        Map<String, List<String>> fieldViolations = FieldExtractor.fieldViolations(ex.getBindingResult());
        ErrorCode errorCode = ErrorCode.CMM_FIELD_VALIDATION_FAILURE;
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), ex.getStatusCode(), fieldViolations);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(CustomException ex) {
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), errorCode.getHttpStatus(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(DataIntegrityViolationException exception) {
        CustomException ex = new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), errorCode.getHttpStatus(), null);
    }

    private <T> ResponseEntity<CustomErrorResponse<T>> createResponseEntity(
            String code, String message, HttpStatusCode httpStatus, T errors) {
        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CustomErrorResponse<>(code, message, errors));
    }

    private void logError(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
    }

}
