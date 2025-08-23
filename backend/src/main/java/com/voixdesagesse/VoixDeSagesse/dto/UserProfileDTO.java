package com.voixdesagesse.VoixDeSagesse.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String username;
    private String phoneNumber;
    private String location;
    private String website;
    private String profilePicture;
    @Size(max = 500, message = "{user.bio.exceeded}" )
    private String bio;

    // private Long contentCount;
    // private Long followersCount;
    // private Long followingCount;
    // private Long likesReceived;
    
    
}
