package com.voixdesagesse.VoixDeSagesse.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;


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
    @Indexed(unique = true)  // Index unique pour l'email
    private String email;
    private String motdepasse;
    private AccountType accountType;

    private String profilePicture;
    private String bio;
    private Long contentCount;
    private Long followersCount;
    private Long followingCount;
    private Long likesReceived;

    @DBRef  // Utilisation de DBRef pour indiquer la référence à la collection Article
    private List<Article> articles; 
    

    public UserRegistrationDTO toRegisterDTO() {
        return new UserRegistrationDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.accountType);
    }

    public UserDTO toDTO() {
        return new UserDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.accountType, this.profilePicture,this.bio, this.contentCount, this.followersCount, this.followingCount, this.likesReceived, this.articles);
    }

}