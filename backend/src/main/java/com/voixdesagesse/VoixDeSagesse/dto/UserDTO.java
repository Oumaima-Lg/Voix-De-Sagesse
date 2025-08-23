package com.voixdesagesse.VoixDeSagesse.dto;


import java.util.Set;


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

}
