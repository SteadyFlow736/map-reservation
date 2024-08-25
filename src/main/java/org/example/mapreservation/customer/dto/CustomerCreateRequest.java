package org.example.mapreservation.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.mapreservation.customer.domain.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;

public record CustomerCreateRequest(
        @Email(message = "이메일 형식이 잘못되었습니다.")
        @NotBlank(message = "이메일은 필수 제출 항목입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 제출 항목입니다.")
        @Size(min = 8, max = 16, message = "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.")
        @Pattern(regexp = ".*[A-Z].*", message = "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.")
        @Pattern(regexp = ".*[a-z].*", message = "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.")
        @Pattern(regexp = ".*\\d.*", message = "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.")
        @Pattern(regexp = ".*[@$!%*?&].*", message = "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다.")
        String password
) {

    public Customer toEntity(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        return new Customer(email, encodedPassword);
    }
}
