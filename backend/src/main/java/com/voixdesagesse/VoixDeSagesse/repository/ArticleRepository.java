package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

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
    

}
