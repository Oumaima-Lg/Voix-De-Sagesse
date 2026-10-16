package com.voixdesagesse.VoixDeSagesse.service;

import java.util.List;
import com.voixdesagesse.VoixDeSagesse.dto.HistoireDTO;
import com.voixdesagesse.VoixDeSagesse.dto.PosteDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SagesseDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;

public interface ArticleService {

    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws ArticlaException;

    public Article createHistoireArticle(HistoireDTO histoireDTO) throws ArticlaException;

    public Article getArticleById(Long articleId) throws ArticlaException;

    public List<Article> getAllArticles();

    public List<Article> getArticlesByUserId(long userId);

    public List<PosteDTO> displayPosts(long currentUserId) throws ArticlaException;

    public List<PosteDTO> getSavedArticles(Long currentUserId) throws ArticlaException;

    public List<PosteDTO> getMyPosts(Long currentUserId) throws ArticlaException;

    public void likeArticle(Long articleId, Long currentUserId) throws ArticlaException;

    public void unlikeArticle(Long articleId, Long currentUserId) throws ArticlaException;

    public void deleteArticle(Long articleId, Long currentUserId) throws ArticlaException;

    public List<PosteDTO> searchArticles(Long currentUserId, String searchText, String type) throws ArticlaException;

    public long getTotalArticles();

    public long getNewArticlesToday();

    public void deleteByUserId(Long userId);

}
