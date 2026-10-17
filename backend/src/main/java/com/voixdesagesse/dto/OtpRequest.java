package com.voixdesagesse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank(message = "{user.email.required}") @Email(message = "{user.email.invalid}") String email) {
}
