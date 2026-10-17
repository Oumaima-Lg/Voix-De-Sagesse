package com.voixdesagesse.service;

import java.util.List;

import com.voixdesagesse.dto.CommentDTO;
import com.voixdesagesse.exception.VoixDeSagesseException;

public interface CommentService {
    
    CommentDTO addComment(Long articleId, Long userId, String content) throws VoixDeSagesseException;
    
    List<CommentDTO> getCommentsByArticle(Long articleId) throws VoixDeSagesseException;
    
    void deleteComment(Long commentId, Long userId) throws VoixDeSagesseException;
    
    Long getCommentsCountByArticle(Long articleId);
    

}