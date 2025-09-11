package com.voixdesagesse.VoixDeSagesse.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    
    @Id
    private Long id;
    
    private Long articleId;
    private Long userId;
    private String content;
    private LocalDateTime dateCreation;
    private Boolean isDeleted;

    public Comment(Long articleId, Long userId, String content) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.dateCreation = LocalDateTime.now();
        this.isDeleted = false;
    }
    
}