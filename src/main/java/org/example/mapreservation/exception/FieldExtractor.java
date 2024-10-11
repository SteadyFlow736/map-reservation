package org.example.mapreservation.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldExtractor {

    /**
     * field 별 위반 사항을 BindingResult로부터 추출하여 리턴
     *
     * @param bindingResult 실패한 바인딩 결과
     * @return field 별 위반 메시지
     */
    public static Map<String, List<String>> fieldViolations(BindingResult bindingResult) {
        Map<String, List<String>> violations = new HashMap<>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        fieldErrors.forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            List<String> messages = violations.getOrDefault(fieldName, new ArrayList<>());
            messages.add(errorMessage);
            violations.put(fieldName, messages);
        });
        return violations;
    }
}
