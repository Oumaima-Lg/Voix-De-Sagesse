package com.voixdesagesse.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.dto.HistoireDTO;
import com.voixdesagesse.dto.PosteDTO;
import com.voixdesagesse.dto.SagesseDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/sagesse")
    public ResponseEntity<?> createSagesseArticle(@Valid @RequestBody SagesseDTO sagesseDTO, BindingResult result,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            if (result.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                result.getFieldErrors().forEach(error -> 
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
                );
                return ResponseEntity.badRequest().body(Map.of("errorMessage", errorMessage.toString()));
            }

            sagesseDTO.setUserId(currentUser.getId());
            Article article = articleService.createSagesseArticle(sagesseDTO);
            return new ResponseEntity<>(article, HttpStatus.CREATED);
        } catch (VoixDeSagesseException e) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", e.getMessage()));
        }
    }

    @PostMapping("/histoire")
    public ResponseEntity<?> createHistoireArticle(@Valid @RequestBody HistoireDTO histoireDTO, BindingResult result,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            if (result.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                result.getFieldErrors().forEach(error -> 
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
                );
                return ResponseEntity.badRequest().body(Map.of("errorMessage", errorMessage.toString()));
            }

            histoireDTO.setUserId(currentUser.getId());
            Article article = articleService.createHistoireArticle(histoireDTO);
            return new ResponseEntity<>(article, HttpStatus.CREATED);
        } catch (VoixDeSagesseException e) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", e.getMessage()));
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PosteDTO>> DisplayPosts(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        return ResponseEntity.ok(articleService.displayPosts(currentUser.getId()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        articleService.likeArticle(id, currentUser.getId());
        return ResponseEntity.ok("Article liked successfully");
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<String> unlikeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        articleService.unlikeArticle(id, currentUser.getId());
        return ResponseEntity.ok("Article unliked successfully");
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedArticles(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        List<PosteDTO> savedPosts = articleService.getSavedArticles(currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "savedArticles", savedPosts,
                "count", savedPosts.size(),
                "success", true));
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        List<PosteDTO> myPosts = articleService.getMyPosts(currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "myPosts", myPosts,
                "count", myPosts.size(),
                "success", true));

    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails currentUser)
            throws VoixDeSagesseException {
        articleService.deleteArticle(articleId, currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Article supprimé avec succès",
                "success", true,
                "deletedArticleId", articleId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchArticles(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String type) throws VoixDeSagesseException {
        
        try {
            List<PosteDTO> searchResults = articleService.searchArticles(currentUser.getId(), searchText, type);
            
            return ResponseEntity.ok(searchResults);
        } catch (VoixDeSagesseException e) {
            log.error("Erreur lors de la recherche d'articles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la recherche", "message", e.getMessage()));
        }
    }
}