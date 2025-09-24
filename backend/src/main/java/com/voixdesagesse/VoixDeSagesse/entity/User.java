package com.voixdesagesse.VoixDeSagesse.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")  
public class User {

    @Id
    private Long id;  
    private String nom;
    private String prenom;
    @Indexed(unique = true)  
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
    private LocalDateTime dateInscription;
    

    public UserRegistrationDTO toRegisterDTO() {
        return new UserRegistrationDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.accountType);
    }

    public UserDTO toDTO() {
        return new UserDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.accountType, this.username, 
        this.phoneNumber, this.location, this.website, this.profilePicture,this.bio, this.contentCount, this.followersCount, 
        this.followingCount, this.likesReceived, this.likedArticlesId, this.followingId, this.savedArticlesId, this.dateInscription);
    }

    public UserProfileDTO toProfileDTO() {
        return new UserProfileDTO(this.id, this.nom, this.prenom, this.email, this.username, 
        this.phoneNumber, this.location, this.website, this.profilePicture, this.bio, this.contentCount, this.followersCount, 
        this.followingCount, this.likesReceived);
    }

}