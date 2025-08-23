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
    // String nom;
    // String prenom;
    // String username;
    // String profilePicture;
    UserProfileDTO user;
    PostInteractionDTO interaction;
    // boolean isLiked;
    // boolean isFollowing;
    String createdAt;

}
