package com.voixdesagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.voixdesagesse.dto.ArticleType;
import com.voixdesagesse.dto.HistoireDTO;
import com.voixdesagesse.dto.PostInteractionDTO;
import com.voixdesagesse.dto.PosteDTO;
import com.voixdesagesse.dto.SagesseDTO;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.dto.UserProfileDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserService userService;

    private final CommonService commonService;

    private final SignalRepository signalRepository;


    @Override
    public Article createSagesseArticle(SagesseDTO sagesseDTO) throws VoixDeSagesseException {
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

    @Override
    public Article createHistoireArticle(HistoireDTO histoireDTO) throws VoixDeSagesseException {
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

    @Override
    public Article getArticleById(Long articleId) throws VoixDeSagesseException {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));
    }

    @Override
    public List<PosteDTO> displayPosts(long currentUserId) throws VoixDeSagesseException {
        List<Article> articles = articleRepository.findAll();
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        Set<Long> reportedArticles = currentUser.getReportedArticlesId(); 

        List<PosteDTO> posts = articles.stream()
                .filter(article -> reportedArticles == null || !reportedArticles.contains(article.getId()))
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);
                    User user;
                    try {
                        user = userService.getUserById(article.getUserId());
                        UserProfileDTO userProfileDTO = user.toPublicProfileDTO();
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (VoixDeSagesseException ex) {
                        log.error("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .collect(Collectors.toList());

        return posts;
    }

    @Override
    public List<PosteDTO> getSavedArticles(Long currentUserId) throws VoixDeSagesseException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> followingUsers = currentUser.getFollowingId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        Set<Long> reportedArticles = currentUser.getReportedArticlesId();

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
                        UserProfileDTO userProfileDTO = user.toPublicProfileDTO();
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (VoixDeSagesseException ex) {
                        log.error("Error fetching user: " + ex.getMessage());
                    }

                    return posteDTO;
                })
                .sorted((p1, p2) -> p2.getArticle().getDatePublication()
                        .compareTo(p1.getArticle().getDatePublication()))
                .collect(Collectors.toList());

        return posts;
    }

    @Override
    public List<PosteDTO> getMyPosts(Long currentUserId) throws VoixDeSagesseException {
        User currentUser = userService.getUserById(currentUserId);
        Set<Long> likedArticles = currentUser.getLikedArticlesId();
        Set<Long> savedArticles = currentUser.getSavedArticlesId();
        List<Article> myArticles = articleRepository.findByUserIdOrderByDatePublicationDesc(currentUserId);

        List<PosteDTO> posts = myArticles.stream()
                .map(article -> {
                    PosteDTO posteDTO = new PosteDTO();
                    posteDTO.setArticle(article);

                    try {
                        UserProfileDTO userProfileDTO = currentUser.toPublicProfileDTO();
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

    @Override
    public void likeArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));
        // Recorded atomically: a double click cannot count the like twice.
        if (!userService.addLikedArticle(currentUserId, articleId))
            throw new VoixDeSagesseException("ALREADY_LIKED");
        articleRepository.incrementLikes(articleId);
        userService.incrementLikesReceived(article.getUserId());
    }

    @Override
    public void unlikeArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));
        if (!userService.removeLikedArticle(currentUserId, articleId))
            throw new VoixDeSagesseException("NOT_LIKED");
        articleRepository.decrementLikes(articleId);
        userService.decrementLikesReceived(article.getUserId());
    }

    @Override
    public void deleteArticle(Long articleId, Long currentUserId) throws VoixDeSagesseException {
        Article article = articleRepository.findByIdAndUserId(articleId, currentUserId)
                // Same answer whether the article does not exist or belongs to someone else.
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));

        Long likesCount = article.getLikes();
        Long commentsCount = article.getComments();
        Long sharesCount = article.getShares();

        userService.removeArticleFromAllUsersLikes(articleId);
        userService.removeArticleFromAllUsersSaved(articleId);

        if (likesCount > 0)
            userService.decrementLikesReceivedByAmount(currentUserId, likesCount);

        userService.decrementContentCount(currentUserId);
        commonService.deleteAllCommentsByArticle(articleId);
        articleRepository.deleteById(articleId);
        // Reports still waiting on it have nothing left to review (when the admin deletes it, they are already settled).
        signalRepository.settlePendingByArticleId(articleId, SignalStatus.CLOSED, LocalDateTime.now(), null);

        log.info("Article supprimé - ID: %d, Likes: %d, Commentaires: %d, Partages: %d%n",
                articleId, likesCount, commentsCount, sharesCount);
    }

    @Override
    public List<PosteDTO> searchArticles(Long currentUserId, String searchText, String type) throws VoixDeSagesseException {
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
                        UserProfileDTO userProfileDTO = user.toPublicProfileDTO();
                        posteDTO.setUser(userProfileDTO);
                        posteDTO.setCreatedAt(Utilities.getElapsedTime(article.getDatePublication()));

                        PostInteractionDTO interaction = new PostInteractionDTO();
                        interaction.setLiked(likedArticles != null && likedArticles.contains(article.getId()));
                        interaction.setFollowing(followingUsers != null && followingUsers.contains(user.getId()));
                        interaction.setSaved(savedArticles != null && savedArticles.contains(article.getId()));
                        posteDTO.setInteraction(interaction);
                    } catch (VoixDeSagesseException ex) {
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

    @Override
    public long getTotalArticles() {
        return articleRepository.count();
    }

    @Override
    public long getNewArticlesToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return articleRepository.countByDatePublicationBetween(startOfDay, endOfDay);
    }

    @Override
    public void deleteByUserId(Long userId) {
        articleRepository.deleteByUserId(userId);
    }

}
