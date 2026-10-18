package com.voixdesagesse.dto;

import java.time.LocalDateTime;

import com.voixdesagesse.entity.Notification;
import com.voixdesagesse.entity.User;

public record NotificationDTO(Long id, NotificationType type, boolean read, LocalDateTime createdAt, Actor actor,
        Long articleId, String articleExcerpt, String commentExcerpt) {

    // Who did the action (null for moderation decisions).
    public record Actor(Long id, String nom, String prenom, String profilePicture) {
    }

    public static NotificationDTO from(Notification notification, User actor) {
        return new NotificationDTO(notification.getId(), notification.getType(), notification.isRead(),
                notification.getCreatedAt(),
                actor == null ? null : new Actor(actor.getId(), actor.getNom(), actor.getPrenom(), actor.getProfilePicture()),
                notification.getArticleId(), notification.getArticleExcerpt(), notification.getCommentExcerpt());
    }
}
