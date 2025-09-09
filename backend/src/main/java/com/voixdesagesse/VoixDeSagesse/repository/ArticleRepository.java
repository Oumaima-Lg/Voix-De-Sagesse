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

    // Incrémenter les likes
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likes': 1 } }")
    void incrementLikes(Long id);

    // Décrémenter les likes (en évitant les valeurs négatives)
    @Query("{ '_id': ?0, 'likes': { $gt: 0 } }")
    @Update("{ '$inc': { 'likes': -1 } }")
    void decrementLikes(Long id);

    // ✅ Nouvelle méthode pour récupérer les articles d'un utilisateur triés par date
    List<Article> findByUserIdOrderByDatePublicationDesc(Long userId);
    
    // ✅ Compter les articles d'un utilisateur (si pas déjà existant)
    Long countByUserId(Long userId);
    
    // Vérifier l'existence et la propriété d'un article
    Optional<Article> findByIdAndUserId(Long articleId, Long userId);
    

}
