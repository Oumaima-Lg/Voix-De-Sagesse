package com.voixdesagesse.VoixDeSagesse.service;

import java.util.List;

import com.voixdesagesse.VoixDeSagesse.dto.CommentDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;

public interface CommentService {
    
    CommentDTO addComment(Long articleId, Long userId, String content) throws ArticlaException;
    
    List<CommentDTO> getCommentsByArticle(Long articleId) throws ArticlaException;
    
    void deleteComment(Long commentId, Long userId) throws ArticlaException;
    
    Long getCommentsCountByArticle(Long articleId);
    

}