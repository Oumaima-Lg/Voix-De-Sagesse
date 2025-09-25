package com.voixdesagesse.VoixDeSagesse.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalDTO {
    private Long id;
    private Long reporterId;
    private String reporterName;
    private Long reportedUserId;
    private String reportedUserName;
    private Long articleId;
    private String articleTitle;
    private String reason;
    private String description;
    private SignalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Long adminId;
    private String adminComment;
}