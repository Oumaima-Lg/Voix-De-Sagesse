package com.voixdesagesse.VoixDeSagesse.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private Long articleId;
    private UserProfileDTO user;
    private String content;
    private String createdAt; 
    private LocalDateTime dateCreation;
    
}
