package com.voixdesagesse.VoixDeSagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "signals")
public class Signal {
    
    @Id
    private Long id;
    
    private Long reporterId; // ID de l'utilisateur qui signale
    private Long reportedUserId; // ID de l'utilisateur signalé (auteur de l'article)
    private Long articleId; // ID de l'article signalé
    private String reason; // Raison du signalement
    private String description; // Description optionnelle
    private SignalStatus status; // Statut du signalement
    private LocalDateTime createdAt; // Date de création
    private LocalDateTime processedAt; // Date de traitement
    private Long adminId; // ID de l'admin qui traite
    private String adminComment; // Commentaire de l'admin
}