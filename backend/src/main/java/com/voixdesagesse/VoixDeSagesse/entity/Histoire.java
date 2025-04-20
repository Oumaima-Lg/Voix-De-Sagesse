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
// @DiscriminatorValue("Histoire")
// @Document(collection = "histoires")
@Document(collection = "articles")
public class Histoire extends Article {

    private String typeHistoire;

    private String leconTiree;

    private String image;

    public Histoire() {
        super();
        this.setType("Histoire");
    }

}
