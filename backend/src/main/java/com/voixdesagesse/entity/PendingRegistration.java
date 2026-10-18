package com.voixdesagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// A sign-up waiting for its email code. The account is only created in `users` once the code is confirmed.
@Document(collection = "pending_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRegistration {
    @Id
    private String email;
    private String nom;
    private String prenom;
    // Already hashed: the plain password is never stored.
    private String motdepasse;
    private String otpCode;
    private LocalDateTime creationTime;
    private int attempts;
}
