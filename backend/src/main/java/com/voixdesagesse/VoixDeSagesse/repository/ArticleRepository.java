package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.entity.Article;

@Repository
public interface ArticleRepository extends MongoRepository<Article, Long> {

    public List<Article> findByUserId(long userId);

    // Incrémenter les likes
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likes': 1 } }")
    void incrementLikes(Long id);

    // Décrémenter les likes (en évitant les valeurs négatives)
    @Query("{ '_id': ?0, 'likes': { $gt: 0 } }")
    @Update("{ '$inc': { 'likes': -1 } }")
    void decrementLikes(Long id);
    
    // Recherche des articles par titre
    // List<Article> findByTitreContaining(String titre);

    // // Recherche des articles avec un certain nombre de likes (par exemple, plus de 100)
    // List<Article> findByLikesGreaterThan(Long likes);
}
