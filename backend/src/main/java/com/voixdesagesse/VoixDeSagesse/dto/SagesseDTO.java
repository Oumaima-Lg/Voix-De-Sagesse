package com.voixdesagesse.VoixDeSagesse.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import com.voixdesagesse.VoixDeSagesse.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagesseDTO {

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 10, max = 1000, message = "Le contenu doit contenir entre 10 et 1000 caractères")
    private String content;
    
    @Size(max = 200, message = "La source ne peut pas dépasser 200 caractères")
    private String source;
    
    @NotNull(message = "La catégorie est obligatoire")
    private CategoryType category;
    
    @Size(min = 1, message = "Au moins un tag est requis")
    private Set<String> tags;
    
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private long userId;

    public Article toEntity() {
        Article article = new Article();
        article.setCategory(category);
        article.setContent(content);
        article.setSource(source);
        article.setTags(tags);
        article.setUserId(userId);
        return article;
    }
}