package com.voixdesagesse.VoixDeSagesse.dto;

import java.util.Set;

import com.voixdesagesse.VoixDeSagesse.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagesseDTO {

    private String content;
    private String source;
    private CategoryType category;
    private Set<String> tags;
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


//Test Postman:
// {
//     "content" : "cc",
//     "source" : "ss",
//     "category" : "cat",
//     "tags" : "tt",
//     "userId" : 
// }