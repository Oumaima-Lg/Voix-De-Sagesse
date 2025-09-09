package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    // ✅ Cache pour éviter les double-clics (en mémoire)
    private final ConcurrentHashMap<String, Long> likeOperationCache = new ConcurrentHashMap<>();


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
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getArticlesByUserId(long userId) {
        return articleRepository.findByUserId(userId);
    }

    
    // ✅ Mise à jour de la méthode displayPosts pour inclure l'état saved
    public List<PosteDTO> displayPosts(long currentUserId) throws ArticlaException {
        List<Article> articles = articleRepository.findAll();
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId(); // ✅ Ajout

        List<PosteDTO> posts = articles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(), user.getPrenom(), 
                            user.getEmail(), user.getUsername(), user.getPhoneNumber(), user.getLocation(), 
                            user.getWebsite(), user.getProfilePicture(), user.getBio(), user.getContentCount(), 
                            user.getFollowersCount(), user.getFollowingCount(), user.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        
                        PostInteractionDTO interaction = new PostInteractionDTO();
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));
                        
                        // Vérifier si liké
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        
                        // Vérifier si on suit l'auteur
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        
                        // ✅ Vérifier si sauvegardé
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        System.out.println("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .toList();

        return posts;
    }

    // ✅ Version corrigée de likeArticle avec protection contre les doublons
    @Transactional
    public synchronized void likeArticle(Long articleId, Long currentUserId) throws ArticlaException  {
        String cacheKey = articleId + "-" + currentUserId;
        
        // ✅ Vérifier si une opération est déjà en cours
        if (likeOperationCache.containsKey(cacheKey)) {
            long lastOperation = likeOperationCache.get(cacheKey);
            if (System.currentTimeMillis() - lastOperation < 1000) { // 1 seconde de délai
                throw new RuntimeException("Opération trop rapide, veuillez patienter");
            }
        }
        
        // ✅ Marquer l'opération en cours
        likeOperationCache.put(cacheKey, System.currentTimeMillis());
        
        try {
            // ✅ Vérifier l'état actuel avant de faire quoi que ce soit
            User currentUser = userService.getUserById(currentUserId);
            Set<Long> likedArticles = currentUser.getLikedArticlesId();
            
            if (likedArticles != null && likedArticles.contains(articleId)) {
                throw new RuntimeException("Article déjà liké");
            }
            
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

            Long authorId = article.getUserId();

            // ✅ Opérations atomiques
            articleRepository.incrementLikes(articleId);
            userService.addLikedArticle(currentUserId, articleId);
            userService.incrementLikesReceived(authorId);
            
        } finally {
            // ✅ Nettoyer le cache après un délai
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // 2 secondes
                    likeOperationCache.remove(cacheKey);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }


    // ✅ Version corrigée de unlikeArticle avec protection contre les doublons
    @Transactional
    public synchronized void unlikeArticle(Long articleId, Long currentUserId) throws ArticlaException {
        String cacheKey = articleId + "-" + currentUserId + "-unlike";
        
        // ✅ Vérifier si une opération est déjà en cours
        if (likeOperationCache.containsKey(cacheKey)) {
            long lastOperation = likeOperationCache.get(cacheKey);
            if (System.currentTimeMillis() - lastOperation < 1000) { // 1 seconde de délai
                throw new RuntimeException("Opération trop rapide, veuillez patienter");
            }
        }
        
        // ✅ Marquer l'opération en cours
        likeOperationCache.put(cacheKey, System.currentTimeMillis());
        
        try {
            // ✅ Vérifier l'état actuel avant de faire quoi que ce soit
            User currentUser = userService.getUserById(currentUserId);
            Set<Long> likedArticles = currentUser.getLikedArticlesId();
            
            if (likedArticles == null || !likedArticles.contains(articleId)) {
                throw new RuntimeException("Article pas encore liké");
            }
            
            Article article =  articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

            Long authorId = article.getUserId();

            // ✅ Opérations atomiques avec vérification
            if (article.getLikes() > 0) {
                articleRepository.decrementLikes(articleId);
            }
            userService.removeLikedArticle(currentUserId, articleId);
            userService.decrementLikesReceived(authorId);
            
        } finally {
            // ✅ Nettoyer le cache après un délai
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // 2 secondes
                    likeOperationCache.remove(cacheKey);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }



    // ✅ Méthode pour récupérer les articles sauvegardés
    public List<PosteDTO> getSavedArticles(Long currentUserId) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();

        // Récupérer uniquement les articles sauvegardés
        List<Article> articles = articleRepository.findAllById(savedArticles);

        List<PosteDTO> posts = articles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(), user.getPrenom(), 
                            user.getEmail(), user.getUsername(), user.getPhoneNumber(), user.getLocation(), 
                            user.getWebsite(), user.getProfilePicture(), user.getBio(), user.getContentCount(), 
                            user.getFollowersCount(), user.getFollowingCount(), user.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        
                        PostInteractionDTO interaction = new PostInteractionDTO();
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));
                        
                        // Vérifier si liké
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        
                        // Vérifier si on suit l'auteur
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        
                        // ✅ Vérifier si sauvegardé (toujours true pour cette liste)
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        System.out.println("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .sorted((p1, p2) -> p2.getArticle().getDatePublication().compareTo(p1.getArticle().getDatePublication())) // Trier par date décroissante
                .toList();

        return posts;
    }

    // ✅ Méthode pour récupérer les postes personnels (avec toutes les interactions)
    public List<PosteDTO> getMyPosts(Long currentUserId) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();

        // Récupérer uniquement les articles de l'utilisateur connecté
        List<Article> myArticles = articleRepository.findByUserIdOrderByDatePublicationDesc(currentUserId);

        List<PosteDTO> posts = myArticles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    
                    try {
                        // L'utilisateur est l'auteur, donc on utilise currentUser
                        UserProfileDTO userProfileDTO = new UserProfileDTO(
                            currentUser.getId(), currentUser.getNom(), currentUser.getPrenom(), 
                            currentUser.getEmail(), currentUser.getUsername(), currentUser.getPhoneNumber(), 
                            currentUser.getLocation(), currentUser.getWebsite(), currentUser.getProfilePicture(), 
                            currentUser.getBio(), currentUser.getContentCount(), currentUser.getFollowersCount(), 
                            currentUser.getFollowingCount(), currentUser.getLikesReceived()
                        );
                        posteDTO.setUser(userProfileDTO);
                        
                        PostInteractionDTO interaction = new PostInteractionDTO();
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));
                        
                        // Vérifier les interactions (même sur ses propres posts)
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(false); // Toujours false car c'est ses propres posts
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        
                        posteDTO.setInteraction(interaction);
                    } catch (Exception ex) {
                        System.out.println("Error setting user data: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .toList();

        return posts;
    }

    // ✅ Méthode pour récupérer les postes d'un utilisateur spécifique (vue depuis un autre utilisateur)
    public List<PosteDTO> getUserPosts(Long targetUserId, Long currentUserId) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        User targetUser = userService.getUserById(targetUserId);
        
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();

        // Récupérer les articles de l'utilisateur cible
        List<Article> userArticles = articleRepository.findByUserIdOrderByDatePublicationDesc(targetUserId);

        List<PosteDTO> posts = userArticles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    
                    try {
                        UserProfileDTO userProfileDTO = new UserProfileDTO(
                            targetUser.getId(), targetUser.getNom(), targetUser.getPrenom(), 
                            targetUser.getEmail(), targetUser.getUsername(), targetUser.getPhoneNumber(), 
                            targetUser.getLocation(), targetUser.getWebsite(), targetUser.getProfilePicture(), 
                            targetUser.getBio(), targetUser.getContentCount(), targetUser.getFollowersCount(), 
                            targetUser.getFollowingCount(), targetUser.getLikesReceived()
                        );
                        posteDTO.setUser(userProfileDTO);
                        
                        PostInteractionDTO interaction = new PostInteractionDTO();
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));
                        
                        // Vérifier les interactions depuis la perspective de l'utilisateur connecté
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(targetUser.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        
                        posteDTO.setInteraction(interaction);
                    } catch (Exception ex) {
                        System.out.println("Error setting user data: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .toList();

        return posts;
    }

    // ✅ Surcharge pour compatibilité (quand on veut juste voir les posts publics)
    public List<PosteDTO> getUserPosts(Long targetUserId) throws ArticlaException {
        return getUserPosts(targetUserId, targetUserId); // Vue comme si c'était ses propres posts
    }

    // ✅ Version alternative si vous voulez plus de contrôle
    @Transactional
    public void deleteArticle(Long articleId, Long currentUserId) throws ArticlaException {
        // Vérifier que l'article existe et appartient à l'utilisateur
        Article article = articleRepository.findByIdAndUserId(articleId, currentUserId)
            .orElseThrow(() -> new ArticlaException("Article introuvable ou vous n'êtes pas autorisé à le supprimer"));
        
        // Statistiques pour le log
        Long likesCount = article.getLikes() != null ? article.getLikes() : 0L;
        Long commentsCount = article.getComments() != null ? article.getComments() : 0L;
        Long sharesCount = article.getShares() != null ? article.getShares() : 0L;
        
        // Nettoyage des données liées
        userService.removeArticleFromAllUsersLikes(articleId);
        userService.removeArticleFromAllUsersSaved(articleId);
        
        if (likesCount > 0) {
            userService.decrementLikesReceivedByAmount(currentUserId, likesCount);
        }
        
        userService.decrementContentCount(currentUserId);
        
        // Suppression de l'article
        articleRepository.deleteById(articleId);
        
        // Log des statistiques
        log.info("Article supprimé - ID: %d, Likes: %d, Commentaires: %d, Partages: %d%n", 
                         articleId, likesCount, commentsCount, sharesCount);
    }

}
