package com.voixdesagesse.VoixDeSagesse.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.dto.ArticleType;
import com.voixdesagesse.VoixDeSagesse.entity.Article;

@Repository
public interface ArticleRepository extends MongoRepository<Article, Long> {

        public List<Article> findByUserId(long userId);

        @Query("{ '_id': ?0 }")
        @Update("{ '$inc': { 'likes': 1 } }")
        void incrementLikes(Long id);

        @Query("{ '_id': ?0, 'likes': { $gt: 0 } }")
        @Update("{ '$inc': { 'likes': -1 } }")
        void decrementLikes(Long id);

        List<Article> findByUserIdOrderByDatePublicationDesc(Long userId);

        Long countByUserId(Long userId);

        Optional<Article> findByIdAndUserId(Long articleId, Long userId);

        @Query("{ $and: [ " +
                        "{ $or: [ " +
                        "  { 'content': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'title': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'lesson': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'tags': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'source': { $regex: ?0, $options: 'i' } } " +
                        "] }, " +
                        "{ 'type': ?1 } " +
                        "] }")
        List<Article> findByContentContainingIgnoreCaseAndType(String searchText, ArticleType type);

        @Query("{ $or: [ " +
                        "  { 'content': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'title': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'lesson': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'tags': { $regex: ?0, $options: 'i' } }, " +
                        "  { 'source': { $regex: ?0, $options: 'i' } } " +
                        "] }")
        List<Article> findByContentContainingIgnoreCase(String searchText);

        List<Article> findByType(ArticleType type);

        @Query(value = "{ 'datePublication': { $gte: ?0 } }", count = true)
        long countByDatePublicationAfter(LocalDateTime date);

}
