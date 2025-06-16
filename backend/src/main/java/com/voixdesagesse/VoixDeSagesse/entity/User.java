package com.voixdesagesse.VoixDeSagesse.entity;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;


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
    private Date dateInscription;
    private String role;
    private String bio;

    private AccountType accountType;

    @DBRef  // Utilisation de DBRef pour indiquer la référence à la collection Article
    private List<Article> articles; 


    public UserDTO toDTO() {
        return new UserDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.dateInscription, this.role, this.bio, this.accountType, this.articles);
    }

}