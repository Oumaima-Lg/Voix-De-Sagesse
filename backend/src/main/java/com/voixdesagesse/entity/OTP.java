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
    private String otpCode;
    private LocalDateTime creationTime;
    private int attempts;
    private String resetToken;

    public OTP(String email, String otpCode, LocalDateTime creationTime) {
        this(email, otpCode, creationTime, 0, null);
    }
}
