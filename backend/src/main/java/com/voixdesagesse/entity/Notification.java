package com.voixdesagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voixdesagesse.dto.NotificationType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@CompoundIndex(name = "recipient_date", def = "{'recipientId': 1, 'createdAt': -1}")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    private Long id;
    private Long recipientId;
    private Long actorId;
    private NotificationType type;
    private Long articleId;
    private Long commentId;
    private String articleExcerpt;
    private String commentExcerpt;
    private boolean read;
    @Indexed(expireAfter = "90d")
    private LocalDateTime createdAt;
}
