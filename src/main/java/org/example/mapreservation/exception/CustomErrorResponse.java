package org.example.mapreservation.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답")
public record CustomErrorResponse<T>(
        @Schema(description = "에러 코드", example = "CMM_FIELD_VALIDATION_FAILURE")
        String code,
        @Schema(description = "에러 메시지", example = "필드 값 검증에 실패했습니다.")
        String message,
        @Schema(description = "에러 세부 사항")
        T errors) {

    public static <T> CustomErrorResponse<T> from(ErrorCode errorCode, T errors) {
        return new CustomErrorResponse<>(errorCode.name(), errorCode.getMessage(), errors);
    }

}
