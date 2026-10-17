package com.voixdesagesse.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.Comment;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommonService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public void deleteAllCommentsByArticle(Long articleId) throws VoixDeSagesseException {
        List<Comment> comments = commentRepository.findByArticleId(articleId);

        comments.forEach(comment -> comment.setIsDeleted(true));
        commentRepository.saveAll(comments);
    }

    public void deleteAllCommentsByUser(Long userId) throws VoixDeSagesseException {
        List<Comment> userComments = commentRepository.findByUserId(userId);

        for (Comment comment : userComments) {
            Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
            if (article != null) {
                article.setComments(Math.max(0, article.getComments() - 1));
                articleRepository.save(article);
            }
            comment.setIsDeleted(true);
            comment.setDateCreation(LocalDateTime.now());
        }

        commentRepository.saveAll(userComments);
    }
    
}
