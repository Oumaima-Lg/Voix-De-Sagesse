package com.voixdesagesse.VoixDeSagesse.dto;

import java.util.List;

import com.voixdesagesse.VoixDeSagesse.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private AccountType accountType;
    private String profilePicture;
    private String bio;
    private Long contentCount;
    private Long followersCount;
    private Long followingCount;
    private Long likesReceived;
    private List<Article> articles; 

}
