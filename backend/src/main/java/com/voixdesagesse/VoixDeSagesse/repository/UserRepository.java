package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    public Optional<User> findByEmail(String email);

    // Ajouter un article dans la liste des likes
    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'likedArticlesId': ?1 } }")
    void addLikedArticle(Long userId, Long articleId);

    // Retirer un article de la liste des likes
    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'likedArticlesId': ?1 } }")
    void removeLikedArticle(Long userId, Long articleId);

    // Incrémenter le nombre de likes reçus
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likesReceived': 1 } }")
    void incrementLikesReceived(Long userId);

    // Décrémenter le nombre de likes reçus
    @Query("{ '_id': ?0, 'likesReceived': { $gt: 0 } }")
    @Update("{ '$inc': { 'likesReceived': -1 } }")
    void decrementLikesReceived(Long userId);

    // méthodes pour le contentCount
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'contentCount': 1 } }")
    void incrementContentCount(Long userId);

    @Query("{ '_id': ?0, 'contentCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'contentCount': -1 } }")
    void decrementContentCount(Long userId);


    // Nouvelles méthodes pour le système de suivi
    
    // Ajouter un utilisateur dans la liste des suivis (pour celui qui suit)
    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'followingId': ?1 } }")
    void addFollowing(Long currentUserId, Long targetUserId);

    // Retirer un utilisateur de la liste des suivis (pour celui qui suit)
    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'followingId': ?1 } }")
    void removeFollowing(Long currentUserId, Long targetUserId);

    // Incrémenter le nombre de personnes suivies (pour celui qui suit)
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followingCount': 1 } }")
    void incrementFollowingCount(Long userId);

    // Décrémenter le nombre de personnes suivies (pour celui qui suit)
    @Query("{ '_id': ?0, 'followingCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'followingCount': -1 } }")
    void decrementFollowingCount(Long userId);

    // Incrémenter le nombre de followers (pour celui qui est suivi)
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followersCount': 1 } }")
    void incrementFollowersCount(Long userId);

    // Décrémenter le nombre de followers (pour celui qui est suivi)
    @Query("{ '_id': ?0, 'followersCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'followersCount': -1 } }")
    void decrementFollowersCount(Long userId);

    // Méthodes utilitaires pour vérifier les relations
    @Query(value = "{ '_id': ?0, 'followingId': ?1 }", exists = true)
    boolean isFollowing(Long currentUserId, Long targetUserId);

    //  Trouver tous les utilisateurs qui suivent un utilisateur donné
    List<User> findByFollowingIdContaining(Long userId);


    // ✅ Nouvelles méthodes pour la sauvegarde d'articles
    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'savedArticlesId': ?1 } }")
    void addSavedArticle(Long userId, Long articleId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'savedArticlesId': ?1 } }")
    void removeSavedArticle(Long userId, Long articleId);

    // Vérifier si un article est sauvegardé
    @Query(value = "{ '_id': ?0, 'savedArticlesId': ?1 }", exists = true)
    boolean isArticleSaved(Long userId, Long articleId);


    // Retirer un article des likes de TOUS les utilisateurs
    @Query("{ 'likedArticlesId': ?0 }")
    @Update("{ '$pull': { 'likedArticlesId': ?0 } }")
    void removeArticleFromAllUsersLikes(Long articleId);
    
    // Retirer un article des sauvegardes de TOUS les utilisateurs
    @Query("{ 'savedArticlesId': ?0 }")
    @Update("{ '$pull': { 'savedArticlesId': ?0 } }")
    void removeArticleFromAllUsersSaved(Long articleId);
    
    // Décrémenter les likes reçus de l'auteur selon le nombre de likes de l'article
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likesReceived': ?1 } }")
    void decrementLikesReceivedByAmount(Long userId, Long amount);
}
