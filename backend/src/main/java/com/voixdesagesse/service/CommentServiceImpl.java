package com.voixdesagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.voixdesagesse.dto.CommentDTO;
import com.voixdesagesse.dto.UserProfileDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.Comment;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.CommentRepository;
import com.voixdesagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "commentService")
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ArticleRepository articleRepository;

    private final UserService userService;

    @Override
    public CommentDTO addComment(Long articleId, Long userId, String content) throws VoixDeSagesseException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("Article introuvable"));

        User user = userService.getUserById(userId);
        if (content == null || content.trim().isEmpty()) {
            throw new VoixDeSagesseException("Le commentaire ne peut pas être vide");
        }

        if (content.trim().length() > 500) {
            throw new VoixDeSagesseException("Le commentaire ne peut pas dépasser 500 caractères");
        }

        Comment comment = new Comment(articleId, userId, content.trim());
        comment.setId(Utilities.getNextSequence("comments"));
        Comment savedComment = commentRepository.save(comment);

        article.setComments(article.getComments() + 1);
        articleRepository.save(article);

        return convertToDTO(savedComment, user);
    }

    @Override
    public List<CommentDTO> getCommentsByArticle(Long articleId) throws VoixDeSagesseException {
        if (!articleRepository.existsById(articleId)) {
            throw new VoixDeSagesseException("Article introuvable");
        }

        List<Comment> comments = commentRepository.findByArticleIdAndIsDeletedFalse(articleId);

        return comments.stream()
                .map(comment -> {
                    try {
                        User user = userService.getUserById(comment.getUserId());
                        return convertToDTO(comment, user);
                    } catch (VoixDeSagesseException e) {
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws VoixDeSagesseException {
        Comment comment = commentRepository.findByIdAndUserIdAndIsDeletedFalse(commentId, userId);
        if (comment == null) {
            throw new VoixDeSagesseException("Commentaire introuvable ou vous n'êtes pas autorisé à le supprimer");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);

        Article article = articleRepository.findById(comment.getArticleId())
                .orElseThrow(() -> new VoixDeSagesseException("Article introuvable"));

        article.setComments(Math.max(0, article.getComments() - 1));
        articleRepository.save(article);

        System.out.println("Commentaire supprimé: " + commentId + " par utilisateur: " + userId);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldDeletedComments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusWeeks(1);

        List<Comment> oldDeletedComments = commentRepository
                .findByIsDeletedTrueAndDeletedAtBefore(cutoffDate);

        commentRepository.deleteAll(oldDeletedComments);

        log.info("Nettoyage: " + oldDeletedComments.size() + " commentaires purgés");
    }

    private CommentDTO convertToDTO(Comment comment, User user) {
        UserProfileDTO userDTO = user.toPublicProfileDTO();

        String timeAgo = Utilities.getElapsedTime(comment.getDateCreation());

        return new CommentDTO(
                comment.getId(),
                comment.getArticleId(),
                userDTO,
                comment.getContent(),
                timeAgo,
                comment.getDateCreation());
    }

    
}
