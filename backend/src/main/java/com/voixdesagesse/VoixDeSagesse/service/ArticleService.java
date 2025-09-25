package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final UserService userService;

    private final CommentService commentService;

    private final ConcurrentHashMap<String, Long> likeOperationCache = new ConcurrentHashMap<>();

    @Transactional
    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws ArticlaException {
        Article article = sagesseDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setType(ArticleType.SAGESSE);

        Article savedArticle = articleRepository.save(article);
        userService.incrementContentCount(article.getUserId());
        return savedArticle;
    }

    @Transactional
    public Article createHistoireArticle(HistoireDTO histoireDTO) throws ArticlaException {
        Article article = histoireDTO.toEntity();
        article.setId(Utilities.getNextSequence("articles"));
        article.setDatePublication(LocalDateTime.now());
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setType(ArticleType.HISTOIRE);

        Article savedArticle = articleRepository.save(article);
        userService.incrementContentCount(article.getUserId());
        return savedArticle;
    }

    public Article getArticleById(Long articleId) throws ArticlaException {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticlaException("Article non trouvé"));
    }

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
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        Set<Long> reportedArticles = currentUser.getReportedArticlesId(); // Articles signalés

        List<PosteDTO> posts = articles.stream()
                .filter(article -> reportedArticles == null || !reportedArticles.contains(article.getId())) // Filtrer
                                                                                                            // les
                                                                                                            // articles
                                                                                                            // signalés
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(),
                                user.getPrenom(),
                                user.getEmail(), user.getUsername(), user.getPhoneNumber(), user.getLocation(),
                                user.getWebsite(), user.getProfilePicture(), user.getBio(), user.getContentCount(),
                                user.getFollowersCount(), user.getFollowingCount(), user.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        log.error("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .collect(Collectors.toList());

        return posts;
    }

    public List<PosteDTO> getSavedArticles(Long currentUserId) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        Set<Long> reportedArticles = currentUser.getReportedArticlesId(); // Articles signalés

        // Filtrer les articles sauvegardés pour exclure ceux qui ont été signalés
        Set<Long> filteredSavedArticles = savedArticles.stream()
                .filter(articleId -> reportedArticles == null || !reportedArticles.contains(articleId))
                .collect(Collectors.toSet());

        List<Article> articles = articleRepository.findAllById(filteredSavedArticles);

        List<PosteDTO> posts = articles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getNom(),
                                user.getPrenom(),
                                user.getEmail(), user.getUsername(), user.getPhoneNumber(), user.getLocation(),
                                user.getWebsite(), user.getProfilePicture(), user.getBio(), user.getContentCount(),
                                user.getFollowersCount(), user.getFollowingCount(), user.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        log.error("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .sorted((p1, p2) -> p2.getArticle().getDatePublication()
                        .compareTo(p1.getArticle().getDatePublication()))
                .collect(Collectors.toList());

        return posts;
    }

    public List<PosteDTO> getMyPosts(Long currentUserId) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        List<Article> myArticles = articleRepository.findByUserIdOrderByDatePublicationDesc(currentUserId);

        List<PosteDTO> posts = myArticles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);

                    try {
                        UserProfileDTO userProfileDTO = new UserProfileDTO(
                                currentUser.getId(), currentUser.getNom(), currentUser.getPrenom(),
                                currentUser.getEmail(), currentUser.getUsername(), currentUser.getPhoneNumber(),
                                currentUser.getLocation(), currentUser.getWebsite(), currentUser.getProfilePicture(),
                                currentUser.getBio(), currentUser.getContentCount(), currentUser.getFollowersCount(),
                                currentUser.getFollowingCount(), currentUser.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(false);
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (Exception ex) {
                        log.error("Error setting user data: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .toList();

        return posts;
    }

    @Transactional
    public synchronized void likeArticle(Long articleId, Long currentUserId) throws ArticlaException {
        String cacheKey = articleId + "-" + currentUserId;

        if (likeOperationCache.containsKey(cacheKey)) {
            long lastOperation = likeOperationCache.get(cacheKey);
            if (System.currentTimeMillis() - lastOperation < 1000) {
                throw new ArticlaException("Opération trop rapide, veuillez patienter");
            }
        }

        likeOperationCache.put(cacheKey, System.currentTimeMillis());

        try {
            User currentUser = userService.getUserById(currentUserId);
            Set<Long> likedArticles = currentUser.getLikedArticlesId();

            if (likedArticles != null && likedArticles.contains(articleId))
                throw new ArticlaException("Article déjà liké");

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ArticlaException("Article not found"));

            Long authorId = article.getUserId();

            articleRepository.incrementLikes(articleId);
            userService.addLikedArticle(currentUserId, articleId);
            userService.incrementLikesReceived(authorId);

        } finally {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    likeOperationCache.remove(cacheKey);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    @Transactional
    public synchronized void unlikeArticle(Long articleId, Long currentUserId) throws ArticlaException {
        String cacheKey = articleId + "-" + currentUserId + "-unlike";

        if (likeOperationCache.containsKey(cacheKey)) {
            long lastOperation = likeOperationCache.get(cacheKey);
            if (System.currentTimeMillis() - lastOperation < 1000) {
                throw new RuntimeException("Opération trop rapide, veuillez patienter");
            }
        }

        likeOperationCache.put(cacheKey, System.currentTimeMillis());

        try {

            User currentUser = userService.getUserById(currentUserId);
            Set<Long> likedArticles = currentUser.getLikedArticlesId();

            if (likedArticles == null || !likedArticles.contains(articleId)) {
                throw new RuntimeException("Article pas encore liké");
            }

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Article not found"));

            Long authorId = article.getUserId();

            if (article.getLikes() > 0) {
                articleRepository.decrementLikes(articleId);
            }
            userService.removeLikedArticle(currentUserId, articleId);
            userService.decrementLikesReceived(authorId);

        } finally {

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

    @Transactional
    public void deleteArticle(Long articleId, Long currentUserId) throws ArticlaException {
        Article article = articleRepository.findByIdAndUserId(articleId, currentUserId)
                .orElseThrow(
                        () -> new ArticlaException("Article introuvable ou vous n'êtes pas autorisé à le supprimer"));

        Long likesCount = article.getLikes();
        Long commentsCount = article.getComments();
        Long sharesCount = article.getShares();

        userService.removeArticleFromAllUsersLikes(articleId);
        userService.removeArticleFromAllUsersSaved(articleId);

        if (likesCount > 0)
            userService.decrementLikesReceivedByAmount(currentUserId, likesCount);

        userService.decrementContentCount(currentUserId);
        commentService.deleteAllCommentsByArticle(articleId);
        articleRepository.deleteById(articleId);

        log.info("Article supprimé - ID: %d, Likes: %d, Commentaires: %d, Partages: %d%n",
                articleId, likesCount, commentsCount, sharesCount);
    }

    public List<PosteDTO> searchArticles(Long currentUserId, String searchText, String type) throws ArticlaException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();

        List<Article> articles;

        if (searchText != null && !searchText.trim().isEmpty() && type != null && !type.trim().isEmpty()) {
            ArticleType articleType = ArticleType.valueOf(type.toUpperCase());
            articles = articleRepository.findByContentContainingIgnoreCaseAndType(searchText.trim(), articleType);
        } else if (searchText != null && !searchText.trim().isEmpty()) {
            articles = articleRepository.findByContentContainingIgnoreCase(searchText.trim());
        } else if (type != null && !type.trim().isEmpty()) {
            ArticleType articleType = ArticleType.valueOf(type.toUpperCase());
            articles = articleRepository.findByType(articleType);
        } else {
            articles = articleRepository.findAll();
        }

        List<PosteDTO> posts = articles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);

                    try {
                        User user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = new UserProfileDTO(
                                user.getId(), user.getNom(), user.getPrenom(),
                                user.getEmail(), user.getUsername(), user.getPhoneNumber(),
                                user.getLocation(), user.getWebsite(), user.getProfilePicture(),
                                user.getBio(), user.getContentCount(), user.getFollowersCount(),
                                user.getFollowingCount(), user.getLikesReceived());
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (ArticlaException ex) {
                        log.error("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .sorted((p1, p2) -> p2.getArticle().getDatePublication()
                        .compareTo(p1.getArticle().getDatePublication()))
                .toList();

        log.info("Recherche effectuée - Texte: '{}', Type: '{}', Résultats: {}",
                searchText, type, posts.size());

        return posts;
    }

    public long getTotalArticles() {
        return articleRepository.count();
    }

    public long getNewArticlesToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return articleRepository.countByDatePublicationBetween(startOfDay, endOfDay);
    }

}
