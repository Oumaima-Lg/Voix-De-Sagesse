package com.voixdesagesse.VoixDeSagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.HistoireDTO;
import com.voixdesagesse.VoixDeSagesse.dto.PosteDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SagesseDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.ArticleService;

import lombok.RequiredArgsConstructor;



@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RequiredArgsConstructor
@RestController 
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/createSaggesseArticla")
    public ResponseEntity<Article> createArticle(@RequestBody SagesseDTO sagesseDTO) throws ArticlaException {
        return new ResponseEntity<>(articleService.createSagesseArticle(sagesseDTO), HttpStatus.CREATED);
    }

    @PostMapping("/createHistoireArticla")
    public ResponseEntity<Article> createArticle(@RequestBody HistoireDTO histoireDTO) throws ArticlaException {
        return new ResponseEntity<>(articleService.createHistoireArticle(histoireDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Article>> DisplayArticles() throws ArticlaException {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/persoArticle/{userId}")
    public ResponseEntity<List<Article>> DisplayArticlesByUser(@PathVariable long userId) throws ArticlaException {
        return ResponseEntity.ok(articleService.getArticlesByUserId(userId));
    }

    @GetMapping("/posts/{currentUserId}")
    public ResponseEntity<List<PosteDTO>> DisplayPosts(@PathVariable long currentUserId) throws ArticlaException {
        return ResponseEntity.ok(articleService.displayPosts(currentUserId));
    }

   @PostMapping("/{id}/like")
    public ResponseEntity<String> likeArticle (
            @PathVariable Long id,
            @RequestParam Long currentUserId) throws ArticlaException {

        articleService.likeArticle(id, currentUserId);
        return ResponseEntity.ok("Article liked successfully");
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<String> unlikeArticle(
            @PathVariable Long id,
            @RequestParam Long currentUserId) throws ArticlaException {

        articleService.unlikeArticle(id, currentUserId);
        return ResponseEntity.ok("Article unliked successfully");
    }


    @GetMapping("/saved/{userId}")
    public ResponseEntity<?> getSavedArticles(@PathVariable Long userId) throws ArticlaException {
            List<PosteDTO> savedPosts = articleService.getSavedArticles(userId);
            
            return ResponseEntity.ok(Map.of(
                "savedArticles", savedPosts,
                "count", savedPosts.size(),
                "success", true
            ));
    }


    @GetMapping("/my-posts/{userId}")
    public ResponseEntity<?> getMyPosts(@PathVariable Long userId) throws ArticlaException {

            List<PosteDTO> myPosts = articleService.getMyPosts(userId);
            
            return ResponseEntity.ok(Map.of(
                "myPosts", myPosts,
                "count", myPosts.size(),
                "success", true
            ));
       
    }

    @DeleteMapping("/delete/{articleId}/{userId}")
    public ResponseEntity<?> deleteArticleWithUserId(@PathVariable Long articleId, @PathVariable Long userId) throws ArticlaException  {
            articleService.deleteArticle(articleId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Article supprimé avec succès",
                "success", true,
                "deletedArticleId", articleId
            ));
    }
}

