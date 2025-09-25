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
public class HistoireDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String title;
    
    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 50, max = 5000, message = "Le contenu doit contenir entre 50 et 5000 caractères")
    private String content;
    
    @NotNull(message = "La catégorie est obligatoire")
    private CategoryType category;
    
    @Size(min = 1, message = "Au moins un tag est requis")
    private Set<String> tags;
    
    @Size(max = 500, message = "La leçon ne peut pas dépasser 500 caractères")
    private String lesson;
    
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private long userId;

    public Article toEntity() {
        Article article = new Article();
        article.setCategory(category);
        article.setContent(content);
        article.setTitle(title);
        article.setLesson(lesson);
        article.setTags(tags);
        article.setUserId(userId);
        return article;
    }
}