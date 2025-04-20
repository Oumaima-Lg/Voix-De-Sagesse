package com.voixdesagesse.VoixDeSagesse.entity;

// import jakarta.persistence.*;
import java.sql.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @Entity
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Utilisation de l'héritage avec une seule table
// @DiscriminatorColumn(name = "article_type", discriminatorType = DiscriminatorType.STRING) // article_type est le nom de la colonne qui sera utilisée pour distinguer les entités. Cette colonne stocke le type de chaque entité (par exemple, "Sagesse" ou "Histoire").
@Document(collection = "articles")
public class Article {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Id
    private String id;  // L'ID est maintenant un `String`

    // @Column(nullable = false, unique = true)
    private String titre;

    // @Column(nullable = false, unique = true)
    private String contenu;

    private Date datePublication;

    private Long likes;

    @Field("article_type")
    private String type;  // Ce champ contiendra "Sagesse" ou "Histoire"

    // @ManyToOne
    // @JoinColumn(name = "user_id")
    // private User auteur;

    @Field("user_id")  // Champ de référence à l'utilisateur
    private User auteur;


}
