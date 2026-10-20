package com.voixdesagesse.service;

import java.util.List;

import com.voixdesagesse.dto.NotificationDTO;
import com.voixdesagesse.dto.NotificationType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.Comment;

public interface NotificationService {

    void articleLiked(Article article, Long actorId);

    void articleSaved(Article article, Long actorId);

    void articleCommented(Article article, Comment comment);

    void userFollowed(Long followedUserId, Long actorId);

    void articleRemoved(Article article);

    void withdraw(NotificationType type, Long recipientId, Long actorId, Long articleId);

    void commentDeleted(Long commentId);

    void articleDeleted(Long articleId);

    void userDeleted(Long userId);

    List<NotificationDTO> getLatest(Long userId);

    long countUnread(Long userId);

    void markAllRead(Long userId);
}
