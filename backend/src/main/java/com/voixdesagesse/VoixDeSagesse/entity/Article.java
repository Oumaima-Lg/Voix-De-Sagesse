package com.voixdesagesse.VoixDeSagesse.entity;


import java.sql.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private String id;  

    private String titre;
    private String contenu;
    private Date datePublication;
    private Long likes;

    // @Field("article_type")
    // private String type;  // Ce champ contiendra "Sagesse" ou "Histoire"
    
    @Field("user_id")  
    private User auteur;

}
