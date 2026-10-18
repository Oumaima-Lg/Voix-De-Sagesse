package com.voixdesagesse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    
    private Long id; 

    @NotBlank(message = "{user.lastname.absent}")
    private String nom;

    @NotBlank(message = "{user.firstname.absent}")
    private String prenom;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.absent}")
    private String email;

    @NotBlank(message = "{user.password.absent}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "{user.password.invalid}"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String motdepasse;
    // Set by the server on registration, never taken from the request body.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AccountType accountType;

}
