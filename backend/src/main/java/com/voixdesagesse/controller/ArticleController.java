package com.voixdesagesse.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.HistoireDTO;
import com.voixdesagesse.dto.PosteDTO;
import com.voixdesagesse.dto.SagesseDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.ArticleService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Tag(name = "Articles")
@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Publier une sagesse (citation, proverbe…)")
    @PostMapping("/sagesse")
    public ResponseEntity<Article> createSagesseArticle(@Valid @RequestBody SagesseDTO sagesseDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        sagesseDTO.setUserId(currentUser.getId());
        return new ResponseEntity<>(articleService.createSagesseArticle(sagesseDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Publier une histoire (expérience personnelle)")
    @PostMapping("/histoire")
    public ResponseEntity<Article> createHistoireArticle(@Valid @RequestBody HistoireDTO histoireDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        histoireDTO.setUserId(currentUser.getId());
        return new ResponseEntity<>(articleService.createHistoireArticle(histoireDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Fil d'actualité : tous les articles avec leur auteur")
    @GetMapping("/posts")
    public ResponseEntity<List<PosteDTO>> DisplayPosts(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        return ResponseEntity.ok(articleService.displayPosts(currentUser.getId()));
    }

    @Operation(summary = "Aimer un article")
    @PostMapping("/{id}/like")
    public ResponseEntity<String> likeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        articleService.likeArticle(id, currentUser.getId());
        return ResponseEntity.ok("Article liked successfully");
    }

    @Operation(summary = "Retirer son « j'aime »")
    @DeleteMapping("/{id}/like")
    public ResponseEntity<String> unlikeArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        articleService.unlikeArticle(id, currentUser.getId());
        return ResponseEntity.ok("Article unliked successfully");
    }

    @Operation(summary = "Articles en favoris de l'utilisateur connecté")
    @GetMapping("/saved")
    public ResponseEntity<?> getSavedArticles(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        List<PosteDTO> savedPosts = articleService.getSavedArticles(currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "savedArticles", savedPosts,
                "count", savedPosts.size(),
                "success", true));
    }

    @Operation(summary = "Articles publiés par l'utilisateur connecté")
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {

        List<PosteDTO> myPosts = articleService.getMyPosts(currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "myPosts", myPosts,
                "count", myPosts.size(),
                "success", true));

    }

    @Operation(summary = "Supprimer un de ses articles")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails currentUser)
            throws VoixDeSagesseException {
        articleService.deleteOwnArticle(articleId, currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Article supprimé avec succès",
                "success", true,
                "deletedArticleId", articleId));
    }

    @Operation(summary = "Rechercher des articles par texte et par type")
    @GetMapping("/search")
    public ResponseEntity<List<PosteDTO>> searchArticles(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String type) throws VoixDeSagesseException {
        return ResponseEntity.ok(articleService.searchArticles(currentUser.getId(), searchText, type));
    }
}