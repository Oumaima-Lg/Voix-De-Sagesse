package com.voixdesagesse.VoixDeSagesse.controller;

// import com.voixdesagesse.VoixDeSagesse.entity.Article;
// import com.voixdesagesse.VoixDeSagesse.service.ArticleService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/articles")
public class ArticleController {

    // @Autowired
    // private ArticleService articleService;

    // // Créer un article
    // @PostMapping
    // public ResponseEntity<Article> createArticle(@RequestBody Article article) {
    //     Article createdArticle = articleService.createArticle(article);
    //     return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    // }

    // // Récupérer un article par ID
    // @GetMapping("/{id}")
    // public ResponseEntity<Article> getArticleById(@PathVariable String id) {
    //     Optional<Article> article = articleService.getArticleById(id);
    //     return article.map(ResponseEntity::ok)
    //                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    // }

    // // Lister tous les articles
    // @GetMapping
    // public List<Article> getAllArticles() {
    //     return articleService.getAllArticles();
    // }

    // // Mettre à jour un article
    // @PutMapping("/{id}")
    // public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article updatedArticle) {
    //     Article article = articleService.updateArticle(id, updatedArticle);
    //     return article != null ? ResponseEntity.ok(article) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    // }

    // // Supprimer un article
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
    //     articleService.deleteArticle(id);
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    // }

    // // Rechercher un article par titre
    // @GetMapping("/search")
    // public List<Article> findArticlesByTitle(@RequestParam String title) {
    //     return articleService.findArticlesByTitle(title);
    // }
}
