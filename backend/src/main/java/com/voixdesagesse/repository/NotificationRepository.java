package com.voixdesagesse.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.dto.NotificationType;
import com.voixdesagesse.entity.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, Long> {

    // Ids grow with time: they order notifications created in the same millisecond.
    List<Notification> findTop50ByRecipientIdOrderByCreatedAtDescIdDesc(Long recipientId);

    long countByRecipientIdAndReadFalse(Long recipientId);

    boolean existsByRecipientIdAndActorIdAndTypeAndArticleId(Long recipientId, Long actorId, NotificationType type,
            Long articleId);

    void deleteByRecipientIdAndActorIdAndTypeAndArticleId(Long recipientId, Long actorId, NotificationType type,
            Long articleId);

    void deleteByCommentId(Long commentId);

    void deleteByArticleId(Long articleId);

    void deleteByRecipientIdOrActorId(Long recipientId, Long actorId);

    @Query("{ 'recipientId': ?0, 'read': false }")
    @Update("{ '$set': { 'read': true } }")
    long markAllRead(Long recipientId);
}
