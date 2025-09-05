package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Méthode pour créer un article de sagesse avec incrémentation
    @Transactional
    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws ArticlaException {
        Article article = sagesseDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setType(ArticleType.SAGESSE);
        
        // Sauvegarder l'article
        Article savedArticle = articleRepository.save(article);
        
        // Incrémenter le contentCount de l'utilisateur
        userService.incrementContentCount(article.getUserId());
        
        return savedArticle;
    }

     //  Méthode pour créer un article d'histoire avec incrémentation
    @Transactional
    public Article createHistoireArticle(HistoireDTO histoireDTO) throws ArticlaException {
        Article article = histoireDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setType(ArticleType.HISTOIRE);
        
        // Sauvegarder l'article
        Article savedArticle = articleRepository.save(article);
        
        //  Incrémenter le contentCount de l'utilisateur
        userService.incrementContentCount(article.getUserId());
        
        return savedArticle;
    }

    //  Méthode pour supprimer un article avec décrémentation (optionnel)
    @Transactional
    public void deleteArticle(Long articleId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        // Vérifier que l'utilisateur est le propriétaire
        if (article.getUserId() != currentUserId) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cet article");
        }

        articleRepository.deleteById(articleId);
        
        // ✅ Décrémenter le contentCount de l'utilisateur
        userService.decrementContentCount(currentUserId);
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
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getUsername(),
                         user.getPhoneNumber(), user.getLocation(), user.getWebsite() , user.getProfilePicture(), user.getBio(), user.getContentCount(), user.getFollowersCount(), 
                         user.getFollowingCount(), user.getLikesReceived());
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



    @Transactional
    public void likeArticle(Long articleId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        Long authorId = article.getUserId(); // auteur du post

        articleRepository.incrementLikes(articleId);
        userService.addLikedArticle(currentUserId, articleId);
        userService.incrementLikesReceived(authorId);
    }

    @Transactional
    public void unlikeArticle(Long articleId, Long currentUserId) {
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        Long authorId = article.getUserId(); // auteur du post

        articleRepository.decrementLikes(articleId);
        userService.removeLikedArticle(currentUserId, articleId);
        userService.decrementLikesReceived(authorId);
    }

    // public Article updateArticleLikes(long id) {
    //     if (articleRepository.existsById(id)) {
    //     updatedArticle.setId(id);
    //     return articleRepository.save(updatedArticle);
    //     }
    //     return null; // Si l'article n'existe pas
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
