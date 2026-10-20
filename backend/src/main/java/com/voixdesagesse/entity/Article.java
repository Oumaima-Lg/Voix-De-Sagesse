package com.voixdesagesse.entity;


import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.voixdesagesse.dto.ArticleType;
import com.voixdesagesse.dto.CategoryType;
import com.voixdesagesse.utility.Utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private Long id;  

    private String title;
    private String content;
    private String source;
    private CategoryType category;
    private Set<String> tags;
    private String lesson;
    private LocalDateTime datePublication;
    private Long likes;
    private Long comments;
    private Long shares;
     
    @Field("article_type")
    private ArticleType type;  
     
    private long userId;
    private boolean demo;

    public String label() {
        return title != null && !title.isBlank() ? title : Utilities.excerpt(content, 60);
    }
}
