package com.voixdesagesse.VoixDeSagesse.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "articles")
public class Sagesse extends Article {

    private String typeSagesse;

    private String source;

  
}
