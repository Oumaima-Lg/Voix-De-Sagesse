package com.voixdesagesse.VoixDeSagesse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(
        @NotBlank(message = "{user.email.required}") @Email(message = "{user.email.invalid}") String email,
        @NotBlank(message = "{otp.invalid}") @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") String otp) {
}
