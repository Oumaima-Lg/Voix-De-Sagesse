package com.voixdesagesse.service;

import java.util.List;

import com.voixdesagesse.dto.NotificationDTO;
import com.voixdesagesse.dto.NotificationType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.Comment;

// Tells users what others did with their articles and profile. Nobody is notified of their own actions.
public interface NotificationService {

    void articleLiked(Article article, Long actorId);

    void articleSaved(Article article, Long actorId);

    void articleCommented(Article article, Comment comment);

    void userFollowed(Long followedUserId, Long actorId);

    void articleRemoved(Article article);

    // An action was undone (unlike, unfollow, removed from favorites): its notification goes away.
    void withdraw(NotificationType type, Long recipientId, Long actorId, Long articleId);

    void commentDeleted(Long commentId);

    void articleDeleted(Long articleId);

    void userDeleted(Long userId);

    List<NotificationDTO> getLatest(Long userId);

    long countUnread(Long userId);

    void markAllRead(Long userId);
}
