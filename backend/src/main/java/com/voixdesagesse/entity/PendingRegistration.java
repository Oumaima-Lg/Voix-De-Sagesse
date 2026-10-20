package com.voixdesagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pending_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRegistration {
    @Id
    private String email;
    private String nom;
    private String prenom;
    private String motdepasse;
    private String otpCode;
    private LocalDateTime creationTime;
    private int attempts;
}
