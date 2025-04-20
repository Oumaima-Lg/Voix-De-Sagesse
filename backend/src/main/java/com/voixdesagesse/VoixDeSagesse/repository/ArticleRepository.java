package com.voixdesagesse.VoixDeSagesse.repository;

import com.voixdesagesse.VoixDeSagesse.entity.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    // Recherche des articles par titre
    List<Article> findByTitreContaining(String titre);

    // Recherche des articles par auteur
    List<Article> findByAuteurId(String auteurId);

    // Recherche des articles avec un certain nombre de likes (par exemple, plus de 100)
    List<Article> findByLikesGreaterThan(Long likes);
}
