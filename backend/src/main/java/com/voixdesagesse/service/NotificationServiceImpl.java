package com.voixdesagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.voixdesagesse.dto.NotificationDTO;
import com.voixdesagesse.dto.NotificationType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.Comment;
import com.voixdesagesse.entity.Notification;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.NotificationRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int COMMENT_EXCERPT_LENGTH = 100;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void articleLiked(Article article, Long actorId) {
        notifyOnce(NotificationType.LIKE, article.getUserId(), actorId, article);
    }

    @Override
    public void articleSaved(Article article, Long actorId) {
        notifyOnce(NotificationType.SAVE, article.getUserId(), actorId, article);
    }

    @Override
    public void userFollowed(Long followedUserId, Long actorId) {
        notifyOnce(NotificationType.FOLLOW, followedUserId, actorId, null);
    }

    @Override
    public void articleCommented(Article article, Comment comment) {
        Notification notification = build(NotificationType.COMMENT, article.getUserId(), comment.getUserId(), article);
        if (notification == null)
            return;
        notification.setCommentId(comment.getId());
        notification.setCommentExcerpt(Utilities.excerpt(comment.getContent(), COMMENT_EXCERPT_LENGTH));
        save(notification);
    }

    @Override
    public void articleRemoved(Article article) {
        save(build(NotificationType.ARTICLE_REMOVED, article.getUserId(), null, article));
    }

    @Override
    public void withdraw(NotificationType type, Long recipientId, Long actorId, Long articleId) {
        notificationRepository.deleteByRecipientIdAndActorIdAndTypeAndArticleId(recipientId, actorId, type, articleId);
    }

    @Override
    public void commentDeleted(Long commentId) {
        notificationRepository.deleteByCommentId(commentId);
    }

    @Override
    public void articleDeleted(Long articleId) {
        notificationRepository.deleteByArticleId(articleId);
    }

    @Override
    public void userDeleted(Long userId) {
        notificationRepository.deleteByRecipientIdOrActorId(userId, userId);
    }

    @Override
    public List<NotificationDTO> getLatest(Long userId) {
        List<Notification> notifications = notificationRepository.findTop50ByRecipientIdOrderByCreatedAtDescIdDesc(userId);
        List<Long> actorIds = notifications.stream().map(Notification::getActorId).filter(Objects::nonNull).distinct().toList();
        Map<Long, User> actors = userRepository.findAllById(actorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return notifications.stream()
                .map(notification -> NotificationDTO.from(notification, actors.get(notification.getActorId())))
                .toList();
    }

    @Override
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }

    // Liking, saving or following twice (after undoing it) does not pile up notifications.
    private void notifyOnce(NotificationType type, Long recipientId, Long actorId, Article article) {
        Long articleId = article == null ? null : article.getId();
        if (notificationRepository.existsByRecipientIdAndActorIdAndTypeAndArticleId(recipientId, actorId, type, articleId))
            return;
        save(build(type, recipientId, actorId, article));
    }

    // Returns null when there is nobody to notify (an action on one's own article or profile).
    private Notification build(NotificationType type, Long recipientId, Long actorId, Article article) {
        if (recipientId == null || recipientId.equals(actorId))
            return null;
        Notification notification = new Notification();
        notification.setType(type);
        notification.setRecipientId(recipientId);
        notification.setActorId(actorId);
        notification.setCreatedAt(LocalDateTime.now());
        if (article != null) {
            notification.setArticleId(article.getId());
            notification.setArticleExcerpt(article.label());
        }
        return notification;
    }

    // A notification must never make the action that triggered it fail.
    private void save(Notification notification) {
        if (notification == null)
            return;
        try {
            notification.setId(Utilities.getNextSequence("notifications"));
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("Notification {} for user {} not saved: {}", notification.getType(), notification.getRecipientId(), e.getMessage());
        }
    }
}
