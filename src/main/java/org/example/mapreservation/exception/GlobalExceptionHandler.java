package org.example.mapreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(CustomException ex) {
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), errorCode.getHttpStatus(), null);
    }

    /**
     * 컨트롤러의 bean validation 적용된 ModelAttribute, RequestBody 객체 검증 실패 핸들러
     * 타입 변환 실패, 검증 실패 모두 다룬다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse<Map<String, List<String>>>> handleException(MethodArgumentNotValidException ex) {
        logError(ex);
        Map<String, List<String>> fieldViolations = FieldExtractor.fieldViolations(ex.getBindingResult());
        ErrorCode errorCode = ErrorCode.CMM_FIELD_VALIDATION_FAILURE;
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), ex.getStatusCode(), fieldViolations);
    }

    // 예: /api/hairshops/{hairShopId} 에서 Long 타입 hairShopId를 기대하지만(@PathVariable Long hairShopId),
    // Long 타입이 될 수 없는 값이 들어올 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse<MethodArgumentTypeMismatchDetail>> handleCustomException(MethodArgumentTypeMismatchException ex) {
        logError(ex);
        ErrorCode errorCode = ErrorCode.CMM_ARGUMENT_TYPE_MISMATCH;
        MethodArgumentTypeMismatchDetail detail = new MethodArgumentTypeMismatchDetail(ex.getValue(), ex.getRequiredType());
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), errorCode.getHttpStatus(), detail);
    }

    // 존재하지 않는 endpoint 접근 시
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CustomErrorResponse<String>> handleException(NoHandlerFoundException ex) {
        logError(ex);
        ErrorCode errorCode = ErrorCode.CMM_NO_HANDLER_FOUND;
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), ex.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleException(Exception ex) {
        logError(ex);
        return null;
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

    public record MethodArgumentTypeMismatchDetail(
            Object value,
            Object requiredType
    ) {
    }

}
