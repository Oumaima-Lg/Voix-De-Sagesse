package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voixdesagesse.VoixDeSagesse.dto.CommentDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.entity.Comment;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.ArticleRepository;
import com.voixdesagesse.VoixDeSagesse.repository.CommentRepository;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "commentService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ArticleRepository articleRepository;

    private final UserService userService;

    @Override
    public CommentDTO addComment(Long articleId, Long userId, String content) throws ArticlaException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticlaException("Article introuvable"));

        User user = userService.getUserById(userId);
        if (content == null || content.trim().isEmpty()) {
            throw new ArticlaException("Le commentaire ne peut pas être vide");
        }

        if (content.trim().length() > 500) {
            throw new ArticlaException("Le commentaire ne peut pas dépasser 500 caractères");
        }

        Comment comment = new Comment(articleId, userId, content.trim());
        comment.setId(Utilities.getNextSequence("comments"));
        Comment savedComment = commentRepository.save(comment);

        article.setComments(article.getComments() + 1);
        articleRepository.save(article);

        return convertToDTO(savedComment, user);
    }

    @Override
    public List<CommentDTO> getCommentsByArticle(Long articleId) throws ArticlaException {
        if (!articleRepository.existsById(articleId)) {
            throw new ArticlaException("Article introuvable");
        }

        List<Comment> comments = commentRepository.findByArticleIdAndIsDeletedFalse(articleId);

        return comments.stream()
                .map(comment -> {
                    try {
                        User user = userService.getUserById(comment.getUserId());
                        return convertToDTO(comment, user);
                    } catch (ArticlaException e) {
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public Long getCommentsCountByArticle(Long articleId) {
        return commentRepository.countByArticleIdAndIsDeletedFalse(articleId);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws ArticlaException {
        Comment comment = commentRepository.findByIdAndUserIdAndIsDeletedFalse(commentId, userId);
        if (comment == null) {
            throw new ArticlaException("Commentaire introuvable ou vous n'êtes pas autorisé à le supprimer");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);

        Article article = articleRepository.findById(comment.getArticleId())
                .orElseThrow(() -> new ArticlaException("Article introuvable"));

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
        UserProfileDTO userDTO = new UserProfileDTO(
                user.getId(), user.getNom(), user.getPrenom(),
                user.getEmail(), user.getUsername(), user.getPhoneNumber(),
                user.getLocation(), user.getWebsite(), user.getProfilePicture(),
                user.getBio(), user.getContentCount(), user.getFollowersCount(),
                user.getFollowingCount(), user.getLikesReceived());

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
