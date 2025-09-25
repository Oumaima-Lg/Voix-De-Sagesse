package com.voixdesagesse.VoixDeSagesse.repository;

import java.time.LocalDateTime;
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

    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'likedArticlesId': ?1 } }")
    void addLikedArticle(Long userId, Long articleId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'likedArticlesId': ?1 } }")
    void removeLikedArticle(Long userId, Long articleId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likesReceived': 1 } }")
    void incrementLikesReceived(Long userId);

    @Query("{ '_id': ?0, 'likesReceived': { $gt: 0 } }")
    @Update("{ '$inc': { 'likesReceived': -1 } }")
    void decrementLikesReceived(Long userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'contentCount': 1 } }")
    void incrementContentCount(Long userId);

    @Query("{ '_id': ?0, 'contentCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'contentCount': -1 } }")
    void decrementContentCount(Long userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'followingId': ?1 } }")
    void addFollowing(Long currentUserId, Long targetUserId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'followingId': ?1 } }")
    void removeFollowing(Long currentUserId, Long targetUserId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followingCount': 1 } }")
    void incrementFollowingCount(Long userId);

    @Query("{ '_id': ?0, 'followingCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'followingCount': -1 } }")
    void decrementFollowingCount(Long userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'followersCount': 1 } }")
    void incrementFollowersCount(Long userId);

    @Query("{ '_id': ?0, 'followersCount': { $gt: 0 } }")
    @Update("{ '$inc': { 'followersCount': -1 } }")
    void decrementFollowersCount(Long userId);

    @Query(value = "{ '_id': ?0, 'followingId': ?1 }", exists = true)
    boolean isFollowing(Long currentUserId, Long targetUserId);

    List<User> findByFollowingIdContaining(Long userId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'savedArticlesId': ?1 } }")
    void addSavedArticle(Long userId, Long articleId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$pull': { 'savedArticlesId': ?1 } }")
    void removeSavedArticle(Long userId, Long articleId);

    @Query(value = "{ '_id': ?0, 'savedArticlesId': ?1 }", exists = true)
    boolean isArticleSaved(Long userId, Long articleId);

    @Query("{ 'likedArticlesId': ?0 }")
    @Update("{ '$pull': { 'likedArticlesId': ?0 } }")
    void removeArticleFromAllUsersLikes(Long articleId);

    @Query("{ 'savedArticlesId': ?0 }")
    @Update("{ '$pull': { 'savedArticlesId': ?0 } }")
    void removeArticleFromAllUsersSaved(Long articleId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likesReceived': ?1 } }")
    void decrementLikesReceivedByAmount(Long userId, Long amount);

    @Query(value = "{ 'dateInscription': { $gte: ?0 } }", count = true)
    long countByCreationDateAfter(LocalDateTime date);

    long countByDateInscriptionBetween(LocalDateTime start, LocalDateTime end);
}
