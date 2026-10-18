package com.voixdesagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.CommentDTO;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.service.CommentService;
import com.voixdesagesse.service.UserService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Commentaires")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
     private final UserService userService;

    
    @Operation(summary = "Commenter un article")
    @PostMapping("/article/{articleId}")
    public ResponseEntity<?> addComment(@PathVariable Long articleId, @RequestBody Map<String, String> request)
            throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        CommentDTO comment = commentService.addComment(articleId, currentUser.getId(), request.get("content"));

        return ResponseEntity.ok(Map.of(
            "comment", comment,
            "message", "Commentaire ajouté avec succès",
            "success", true
        ));
    }

    @Operation(summary = "Commentaires d'un article")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable Long articleId) throws VoixDeSagesseException {
        List<CommentDTO> comments = commentService.getCommentsByArticle(articleId);

        return ResponseEntity.ok(Map.of(
            "comments", comments,
            "count", comments.size(),
            "success", true
        ));
    }

    @Operation(summary = "Supprimer un commentaire (par son auteur ou l'auteur de l'article)")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        commentService.deleteComment(commentId, currentUser.getId());

        return ResponseEntity.ok(Map.of(
            "message", "Commentaire supprimé avec succès",
            "success", true
        ));
    }
}