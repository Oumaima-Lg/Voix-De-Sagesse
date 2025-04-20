package com.voixdesagesse.VoixDeSagesse.entity;

import java.sql.Date;

import lombok.AllArgsConstructor;
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.CascadeType;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;


@Data
@NoArgsConstructor
@AllArgsConstructor
// @Entity
@Document(collection = "users")  // Collection MongoDB
public class User {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Id
    private Long id;  // MongoDB utilise des `String` pour l'ID (pas `Long` comme dans MySQL)

    private String nom;

    private String prenom;

    // @Column(nullable = false, unique = true)
    @Indexed(unique = true)  // Index unique pour l'email
    private String email;

    // @Column(nullable = false, unique = true)
    private String motdepasse;

    private Date dateInscription;

    private String role;

    private String bio;

    // @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL)
    @DBRef  // Utilisation de DBRef pour indiquer la référence à la collection Article
    private List<Article> articles; 


    public UserDTO toDTO() {
        return new UserDTO(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.dateInscription, this.role, this.bio, this.articles);
    }

}