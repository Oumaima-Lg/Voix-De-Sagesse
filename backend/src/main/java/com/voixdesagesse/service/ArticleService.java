package com.voixdesagesse.service;

import java.util.List;
import com.voixdesagesse.dto.HistoireDTO;
import com.voixdesagesse.dto.PosteDTO;
import com.voixdesagesse.dto.SagesseDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.exception.VoixDeSagesseException;

public interface ArticleService {

    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws VoixDeSagesseException;

    public Article createHistoireArticle(HistoireDTO histoireDTO) throws VoixDeSagesseException;

    public Article getArticleById(Long articleId) throws VoixDeSagesseException;

    public List<Article> getAllArticles();

    public List<Article> getArticlesByUserId(long userId);

    public List<PosteDTO> displayPosts(long currentUserId) throws VoixDeSagesseException;

    public List<PosteDTO> getSavedArticles(Long currentUserId) throws VoixDeSagesseException;

    public List<PosteDTO> getMyPosts(Long currentUserId) throws VoixDeSagesseException;

    public void likeArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException;

    public void unlikeArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException;

    public void deleteArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException;

    public List<PosteDTO> searchArticles(Long currentUserId, String searchText, String type) throws VoixDeSagesseException;

    public long getTotalArticles();

    public long getNewArticlesToday();

    public void deleteByUserId(Long userId);

}
