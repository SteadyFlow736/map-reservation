package org.example.mapreservation.customer.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.service.CustomerService;
import org.example.mapreservation.customer.presentation.request.CustomerCreate;
import org.example.mapreservation.customer.presentation.response.CustomerInfo;
import org.example.mapreservation.exception.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer", description = "customer api")
@Builder
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "create API", description = "고객 계정 생성 ", tags = {"Customer"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "고객 계정 생성 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "필드 검증 실패(요구사항에 맞지 않는 이메일과 비밀번호)",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class), mediaType = "application/json")
            )})
    @PostMapping("/api/customers")
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid CustomerCreate request) {
        customerService.createCustomer(request.email(), request.password());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/api/customers/me")
    public ResponseEntity<CustomerInfo> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(CustomerInfo.from(user));
    }

}
