package com.voixdesagesse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "{user.email.required}") @Email(message = "{user.email.invalid}") String email,
        @NotBlank(message = "{reset.token.absent}") String resetToken,
        @NotBlank(message = "{user.password.absent}")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", message = "{user.password.invalid}")
        String newPassword) {
}
