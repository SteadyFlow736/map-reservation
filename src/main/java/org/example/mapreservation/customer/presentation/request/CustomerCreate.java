package org.example.mapreservation.customer.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "고객 계정 생성 요청 정보")
public record CustomerCreate(
        @Schema(description = "이메일 주소", example = "abc@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email(message = "이메일 형식이 잘못되었습니다.")
        @NotBlank(message = "이메일은 필수 제출 항목입니다.")
        String email,

        @Schema(description = "비밀번호는 8자리 이상 16자리 이하여야 합니다. 그리고 적어도 하나 이상의 대문자, 소문자, 숫자, 특수문자(@$!%*?& 중에서)를 포함해야 합니다.",
                example = "Password1!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수 제출 항목입니다.")
        @Size(min = 8, max = 16, message = "비밀번호 길이는 8자리 이상 16자리 이하여야 합니다.")
        @Pattern(regexp = ".*[A-Z].*", message = "비밀번호는 적어도 하나 이상의 대문자를 포함해야 합니다.")
        @Pattern(regexp = ".*[a-z].*", message = "비밀번호는 적어도 하나 이상의 소문자를 포함해야 합니다.")
        @Pattern(regexp = ".*\\d.*", message = "비밀번호는 적어도 하나 이상의 숫자를 포함해야 합니다.")
        @Pattern(regexp = ".*[@$!%*?&].*", message = "비밀번호는 적어도 다음 특수 문자(@$!%*?&) 중 하나 이상을 포함해야 합니다.")
        String password
) {
    @Builder
    public CustomerCreate(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
