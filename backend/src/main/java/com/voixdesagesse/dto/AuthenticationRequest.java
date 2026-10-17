package com.voixdesagesse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "{user.email.required}")
    private String email;
    @NotBlank(message = "{user.password.absent}")
    private String password;
    
}
