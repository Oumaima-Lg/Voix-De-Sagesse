package com.voixdesagesse.VoixDeSagesse.entity;

import org.springframework.data.mongodb.core.mapping.Document;

// import jakarta.persistence.DiscriminatorValue;
// import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
// @NoArgsConstructor
// @Entity
// @DiscriminatorValue("Sagesse")
// @Document(collection = "sagesses")
@Document(collection = "articles")
public class Sagesse extends Article {

    private String typeSagesse;

    private String source;

     // Constructeur qui initialise le type
     public Sagesse() {
        super();
        this.setType("Sagesse");
    }
  
}
