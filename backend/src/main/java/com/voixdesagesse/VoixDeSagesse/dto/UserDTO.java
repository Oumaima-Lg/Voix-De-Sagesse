package com.voixdesagesse.VoixDeSagesse.dto;


import java.time.LocalDateTime;
import java.util.Set;

import com.voixdesagesse.VoixDeSagesse.entity.User;

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
    private String username;
    private String phoneNumber;
    private String location;
    private String website;
    private String profilePicture;
    private String bio;
    private Long contentCount;
    private Long followersCount;
    private Long followingCount;
    private Long likesReceived;
    private Set<Long> likedArticlesId;
    private Set<Long> followingId;  
    private Set<Long> savedArticlesId;
    private Set<Long> reportedArticlesId;
    private LocalDateTime dateInscription;


    public User toEntity() {
        return new User(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.accountType, this.username,
         this.phoneNumber, this.location, this.website, this.profilePicture, this.bio, this.contentCount, 
         this.followersCount, this.followingCount, this.likesReceived, this.likedArticlesId, this.followingId, this.savedArticlesId, this.reportedArticlesId, this.dateInscription);
    }

}
