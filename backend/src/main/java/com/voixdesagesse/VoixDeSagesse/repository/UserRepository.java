package com.voixdesagesse.VoixDeSagesse.repository;

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
}
