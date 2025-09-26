package com.voixdesagesse.VoixDeSagesse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.entity.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, Long> {
    
    // Récupérer les commentaires d'un article (non supprimés)
    @Query("{ 'articleId': ?0, 'isDeleted': { $ne: true } }")
    List<Comment> findByArticleIdAndIsDeletedFalse(Long articleId);
    
    // Compter les commentaires d'un article (non supprimés)
    @Query(value = "{ 'articleId': ?0, 'isDeleted': { $ne: true } }", count = true)
    Long countByArticleIdAndIsDeletedFalse(Long articleId);
    
    // Récupérer les commentaires d'un utilisateur
    @Query("{ 'userId': ?0, 'isDeleted': { $ne: true } }")
    List<Comment> findByUserIdAndIsDeletedFalse(Long userId);
    
    // Vérifier si un commentaire appartient à un utilisateur
    @Query("{ 'id': ?0, 'userId': ?1, 'isDeleted': { $ne: true } }")
    Comment findByIdAndUserIdAndIsDeletedFalse(Long commentId, Long userId);
    
    // Supprimer tous les commentaires d'un article (soft delete)
    @Query("{ 'articleId': ?0 }")
    List<Comment> findByArticleId(Long articleId);

    // Trouver les commentaires supprimés avant une certaine date
    @Query("{ 'isDeleted': true, 'deletedAt': { $lt: ?0 } }")
    List<Comment> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);

    List<Comment> findByUserId(Long userId);
}