package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.entity.Comment;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.ArticleRepository;
import com.voixdesagesse.VoixDeSagesse.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommonService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public void deleteAllCommentsByArticle(Long articleId) throws ArticlaException {
        List<Comment> comments = commentRepository.findByArticleId(articleId);

        comments.forEach(comment -> comment.setIsDeleted(true));
        commentRepository.saveAll(comments);
    }

    @Transactional
    public void deleteAllCommentsByUser(Long userId) throws ArticlaException {
        List<Comment> userComments = commentRepository.findByUserId(userId);

        for (Comment comment : userComments) {
            // Décrémenter le compteur de commentaires de l'article
            Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
            if (article != null) {
                article.setComments(Math.max(0, article.getComments() - 1));
                articleRepository.save(article);
            }

            // Soft delete du commentaire
            comment.setIsDeleted(true);
            comment.setDateCreation(LocalDateTime.now());
        }

        commentRepository.saveAll(userComments);
    }
    
}
