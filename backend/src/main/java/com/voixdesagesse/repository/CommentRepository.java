package com.voixdesagesse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.entity.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, Long> {
    
    @Query("{ 'articleId': ?0, 'isDeleted': { $ne: true } }")
    List<Comment> findByArticleIdAndIsDeletedFalse(Long articleId);
    
    @Query(value = "{ 'articleId': ?0, 'isDeleted': { $ne: true } }", count = true)
    Long countByArticleIdAndIsDeletedFalse(Long articleId);
    
    @Query("{ 'userId': ?0, 'isDeleted': { $ne: true } }")
    List<Comment> findByUserIdAndIsDeletedFalse(Long userId);
    
    @Query("{ 'articleId': ?0 }")
    List<Comment> findByArticleId(Long articleId);

    @Query("{ 'isDeleted': true, 'deletedAt': { $lt: ?0 } }")
    List<Comment> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);

    List<Comment> findByUserId(Long userId);
}