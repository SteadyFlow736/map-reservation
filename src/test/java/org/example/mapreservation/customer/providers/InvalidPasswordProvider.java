package org.example.mapreservation.customer.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidPasswordProvider implements ArgumentsProvider {

    private final static String BLANK_VIOLATION_MESSAGE = "비밀번호는 필수 제출 항목입니다.";
    private final static String LENGTH_VIOLATION_MESSAGE = "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.";
    private final static String UPPER_CASE_VIOLATION_MESSAGE = "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.";
    private final static String LOWER_CASE_VIOLATION_MESSAGE = "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.";
    private final static String DIGIT_VIOLATION_MESSAGE = "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.";
    private final static String SPECIAL_CHARACTER_VIOLATION_MESSAGE = "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다.";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Passd1!", LENGTH_VIOLATION_MESSAGE), // 7자리
                Arguments.of("Password1!zzzzzzz", LENGTH_VIOLATION_MESSAGE), // 17자리
                Arguments.of("password1!", UPPER_CASE_VIOLATION_MESSAGE), // 대문자 없음
                Arguments.of("PASSWORD1!", LOWER_CASE_VIOLATION_MESSAGE), // 소문자 없음
                Arguments.of("Password!", DIGIT_VIOLATION_MESSAGE), // 숫자 없음
                Arguments.of("Password1", SPECIAL_CHARACTER_VIOLATION_MESSAGE) // 특수문자 없음
        );
    }
}
