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
    private String reporterEmail;
    private Long reportedUserId;
    private String reportedUserName;
    private String reportedUserEmail;
    private Long articleId;
    private String articleTitle;
    private String articleContent;
    private String articleSource;
    private String articleLesson;
    private String articleTags;
    private String articleType;
    private String articleCategory;
    private String reason;
    private String description;
    private SignalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Long adminId;
    private String adminComment;
}