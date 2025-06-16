package com.voixdesagesse.VoixDeSagesse.entity;


import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Document(collection = "articles")
@EqualsAndHashCode(callSuper=true)
public class Histoire extends Article {

    private String typeHistoire;

    private String leconTiree;

    private String image;

}
