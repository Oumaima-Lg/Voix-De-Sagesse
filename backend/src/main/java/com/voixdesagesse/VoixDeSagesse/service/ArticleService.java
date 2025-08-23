package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voixdesagesse.VoixDeSagesse.dto.ArticleType;
import com.voixdesagesse.VoixDeSagesse.dto.HistoireDTO;
import com.voixdesagesse.VoixDeSagesse.dto.PostInteractionDTO;
import com.voixdesagesse.VoixDeSagesse.dto.PosteDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SagesseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.ArticleRepository;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws ArticlaException {
        Article article = sagesseDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setType(ArticleType.SAGESSE);
        return articleRepository.save(article);
    }

    public Article createHistoireArticle(HistoireDTO histoireDTO) throws ArticlaException {
        Article article = histoireDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setType(ArticleType.HISTOIRE);
        return articleRepository.save(article);
    }

    // // Trouver un article par son ID
    // public Optional<Article> getArticleById(String id) {
    // return articleRepository.findById(id);
    // }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getArticlesByUserId(long userId) {
        return articleRepository.findByUserId(userId);
    }

    public List<PosteDTO> displayPosts(long currentUserId) throws ArticlaException {

        List<Article> articles = articleRepository.findAll();
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();

        List<PosteDTO> posts = articles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(), user.getPrenom(), user.getUsername(),
                         user.getPhoneNumber(), user.getLocation(), user.getWebsite() , user.getProfilePicture(), user.getBio());
                        posteDTO.setUser(userProfileDTO);
                        PostInteractionDTO interaction = new PostInteractionDTO();
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));
                        if (!likedArticles.isEmpty() && likedArticles.contains(article.getId())) {
                            interaction.setLiked(true);
                        } else {
                            interaction.setLiked(false);
                        }
                        if (!followingUsers.isEmpty() && followingUsers.contains(user.getId())) {
                            interaction.setFollowing(true);
                        } else {
                            interaction.setFollowing(false);
                        }
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        System.out.println("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .toList();

        return posts;
    }

    // // Mettre à jour un article
    // public Article updateArticle(String id, Article updatedArticle) {
    // if (articleRepository.existsById(id)) {
    // updatedArticle.setId(id);
    // return articleRepository.save(updatedArticle);
    // }
    // return null; // Si l'article n'existe pas
    // }

    // // Supprimer un article
    // public void deleteArticle(String id) {
    // articleRepository.deleteById(id);
    // }

    // // Rechercher des articles par titre
    // public List<Article> findArticlesByTitle(String title) {
    // return articleRepository.findByTitreContaining(title);
    // }
}
