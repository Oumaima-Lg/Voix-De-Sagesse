package com.voixdesagesse.VoixDeSagesse.dto;

import java.util.Set;

import com.voixdesagesse.VoixDeSagesse.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoireDTO {

    private String title;
    private String content;
    private CategoryType category;
    private Set<String> tags;
    private String lesson;
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
