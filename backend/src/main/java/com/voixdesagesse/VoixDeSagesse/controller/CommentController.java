package com.voixdesagesse.VoixDeSagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.CommentDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import com.voixdesagesse.VoixDeSagesse.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    private final UserRepository userRepository;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        return userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur connecté introuvable"));
    }
    
    // ✅ Ajouter un commentaire
    @PostMapping("/add/{articleId}")
    public ResponseEntity<?> addComment(@PathVariable Long articleId, @RequestBody Map<String, String> request) {
        try {
            User currentUser = getCurrentUser();
            String content = request.get("content");
            
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Le contenu du commentaire est requis",
                    "success", false
                ));
            }
            
            CommentDTO comment = commentService.addComment(articleId, currentUser.getId(), content);
            
            return ResponseEntity.ok(Map.of(
                "comment", comment,
                "message", "Commentaire ajouté avec succès",
                "success", true
            ));
            
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Erreur interne du serveur",
                "success", false
            ));
        }
    }
    
    // ✅ Récupérer les commentaires d'un article
    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable Long articleId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByArticle(articleId);
            
            return ResponseEntity.ok(Map.of(
                "comments", comments,
                "count", comments.size(),
                "success", true
            ));
            
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Erreur interne du serveur",
                "success", false
            ));
        }
    }
    
    // ✅ Supprimer un commentaire
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            User currentUser = getCurrentUser();
            commentService.deleteComment(commentId, currentUser.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Commentaire supprimé avec succès",
                "success", true
            ));
            
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Erreur interne du serveur",
                "success", false
            ));
        }
    }
    
    // ✅ Obtenir le nombre de commentaires d'un article
    @GetMapping("/count/{articleId}")
    public ResponseEntity<?> getCommentsCount(@PathVariable Long articleId) {
        try {
            Long count = commentService.getCommentsCountByArticle(articleId);
            
            return ResponseEntity.ok(Map.of(
                "count", count,
                "success", true
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Erreur interne du serveur",
                "success", false
            ));
        }
    }
}