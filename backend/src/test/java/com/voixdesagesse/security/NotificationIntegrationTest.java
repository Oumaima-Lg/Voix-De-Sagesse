package com.voixdesagesse.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.ArticleType;
import com.voixdesagesse.dto.CategoryType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.NotificationRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.repository.UserRepository;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import tools.jackson.databind.json.JsonMapper;

/** What other users do with an article or a profile reaches its owner as a notification. */
@SpringBootTest
@AutoConfigureMockMvc
class NotificationIntegrationTest {

    private static final long AUTHOR_ID = 9_801L;
    private static final long READER_ID = 9_802L;
    private static final long ADMIN_ID = 9_803L;
    private static final long ARTICLE_ID = 9_901L;
    private static final String CONTENT = "La patience est un arbre dont la racine est amere, mais dont les fruits sont doux.";
    private static final String EXCERPT = "La patience est un arbre dont la racine est amere, mais dont…";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SignalRepository signalRepository;
    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        signalRepository.deleteAll();
        articleRepository.deleteById(ARTICLE_ID);
        userRepository.deleteAllById(List.of(AUTHOR_ID, READER_ID, ADMIN_ID));
        saveUser(AUTHOR_ID, "author-notif@test.local", "Sara", AccountType.USER);
        saveUser(READER_ID, "reader-notif@test.local", "Karim", AccountType.USER);
        saveUser(ADMIN_ID, "admin-notif@test.local", "Admin", AccountType.ADMIN);

        Article article = new Article();
        article.setId(ARTICLE_ID);
        article.setContent(CONTENT);
        article.setCategory(CategoryType.philosophie);
        article.setType(ArticleType.SAGESSE);
        article.setUserId(AUTHOR_ID);
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setDatePublication(LocalDateTime.now());
        articleRepository.save(article);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    @DisplayName("Like, commentaire, favori et abonnement notifient l'auteur, du plus récent au plus ancien")
    void reactionsNotifyTheAuthor() throws Exception {
        like(READER_ID);
        comment(READER_ID, "Très belle pensée, merci.");
        mvc.perform(post("/users/me/saved/" + ARTICLE_ID).with(as(READER_ID))).andExpect(status().isOk());
        mvc.perform(post("/users/" + AUTHOR_ID + "/follow").with(as(READER_ID))).andExpect(status().isOk());

        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(4));
        mvc.perform(get("/notifications").with(as(AUTHOR_ID)))
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].type").value("FOLLOW"))
            .andExpect(jsonPath("$[1].type").value("SAVE"))
            .andExpect(jsonPath("$[2].type").value("COMMENT"))
            .andExpect(jsonPath("$[2].commentExcerpt").value("Très belle pensée, merci."))
            .andExpect(jsonPath("$[3].type").value("LIKE"))
            .andExpect(jsonPath("$[3].actor.prenom").value("Karim"))
            .andExpect(jsonPath("$[3].actor.email").doesNotExist())
            .andExpect(jsonPath("$[3].articleExcerpt").value(EXCERPT))
            .andExpect(jsonPath("$[3].read").value(false));
        // The reader received nothing.
        unreadCount(READER_ID).andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("Ses propres actions ne créent pas de notification")
    void ownActionsAreSilent() throws Exception {
        like(AUTHOR_ID);
        comment(AUTHOR_ID, "Je complète mon article.");
        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("Annuler une réaction retire sa notification, et la refaire n'en crée qu'une")
    void undoingRemovesTheNotification() throws Exception {
        like(READER_ID);
        mvc.perform(delete("/articles/" + ARTICLE_ID + "/like").with(as(READER_ID))).andExpect(status().isOk());
        mvc.perform(post("/users/" + AUTHOR_ID + "/follow").with(as(READER_ID))).andExpect(status().isOk());
        mvc.perform(delete("/users/" + AUTHOR_ID + "/follow").with(as(READER_ID))).andExpect(status().isOk());
        long commentId = comment(READER_ID, "Un commentaire retiré ensuite.");
        mvc.perform(delete("/comments/" + commentId).with(as(READER_ID))).andExpect(status().isOk());
        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(0));

        like(READER_ID);
        mvc.perform(delete("/articles/" + ARTICLE_ID + "/like").with(as(READER_ID)));
        like(READER_ID);
        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("Tout marquer comme lu remet le compteur à zéro sans effacer la liste")
    void markAllRead() throws Exception {
        like(READER_ID);
        comment(READER_ID, "Bravo.");

        mvc.perform(post("/notifications/read-all").with(as(AUTHOR_ID))).andExpect(status().isNoContent());

        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(0));
        mvc.perform(get("/notifications").with(as(AUTHOR_ID)))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].read").value(true));
    }

    @Test
    @DisplayName("Article supprimé par l'admin : l'auteur est notifié, les autres notifications de l'article disparaissent")
    void moderationNotifiesTheAuthor() throws Exception {
        like(READER_ID);
        String json = mvc.perform(post("/signals").with(as(READER_ID)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"articleId\":" + ARTICLE_ID + ",\"reason\":\"Spam\",\"description\":\"\"}"))
            .andReturn().getResponse().getContentAsString();
        long signalId = new JsonMapper().readTree(json).get("id").asLong();

        mvc.perform(put("/admin/signals/" + signalId + "/process").param("status", "APPROVED").with(as(ADMIN_ID)))
            .andExpect(status().isOk());

        mvc.perform(get("/notifications").with(as(AUTHOR_ID)))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].type").value("ARTICLE_REMOVED"))
            .andExpect(jsonPath("$[0].actor").doesNotExist())
            .andExpect(jsonPath("$[0].articleExcerpt").value(EXCERPT));
    }

    @Test
    @DisplayName("Un article supprimé par son auteur emporte ses notifications")
    void deletedArticleTakesItsNotifications() throws Exception {
        like(READER_ID);
        mvc.perform(delete("/articles/" + ARTICLE_ID).with(as(AUTHOR_ID))).andExpect(status().isOk());
        unreadCount(AUTHOR_ID).andExpect(jsonPath("$.count").value(0));
    }

    private void like(long userId) throws Exception {
        mvc.perform(post("/articles/" + ARTICLE_ID + "/like").with(as(userId))).andExpect(status().isOk());
    }

    private long comment(long userId, String content) throws Exception {
        String json = mvc.perform(post("/comments/article/" + ARTICLE_ID).with(as(userId))
                .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"" + content + "\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new JsonMapper().readTree(json).get("comment").get("id").asLong();
    }

    private ResultActions unreadCount(long userId) throws Exception {
        return mvc.perform(get("/notifications/unread-count").with(as(userId))).andExpect(status().isOk());
    }

    private RequestPostProcessor as(long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return user(new CustomUserDetails(u.getId(), u.getNom(), u.getPrenom(), null, u.getEmail(), u.getMotdepasse(),
            u.getAccountType()));
    }

    private void saveUser(long id, String email, String prenom, AccountType type) {
        User user = new User();
        user.setId(id);
        user.setNom("Test");
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotdepasse("not-used");
        user.setAccountType(type);
        user.setContentCount(1L);
        user.setLikesReceived(0L);
        user.setFollowersCount(0L);
        user.setFollowingCount(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user.setReportedArticlesId(new HashSet<>());
        userRepository.save(user);
    }
}
