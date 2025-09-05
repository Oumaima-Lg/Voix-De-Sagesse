package com.voixdesagesse.VoixDeSagesse.dto;

import com.voixdesagesse.VoixDeSagesse.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  PosteDTO {

    Article article;
    UserProfileDTO user;
    PostInteractionDTO interaction;
    String createdAt;

}
