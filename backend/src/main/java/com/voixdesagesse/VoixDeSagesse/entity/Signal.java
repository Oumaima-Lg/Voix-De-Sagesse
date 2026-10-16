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
    
    private Long reporterId; 
    private Long reportedUserId; 
    private Long articleId; 
    private String reason; 
    private String description;
    private SignalStatus status;
    private LocalDateTime createdAt; 
    private LocalDateTime processedAt; 
    private Long adminId; 
    private String adminComment; 
}