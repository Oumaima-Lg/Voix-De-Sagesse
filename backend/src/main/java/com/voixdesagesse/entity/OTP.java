package com.voixdesagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    @Id
    private String email;
    // Cleared once verified, so the same code cannot be used twice.
    private String otpCode;
    private LocalDateTime creationTime;
    private int attempts;
    // Single-use token given after a successful OTP check, required to set the new password.
    private String resetToken;

    public OTP(String email, String otpCode, LocalDateTime creationTime) {
        this(email, otpCode, creationTime, 0, null);
    }
}
