package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.ArticleType;
import com.voixdesagesse.dto.CategoryType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationIntegrationTest {

    private static final long ALICE_ID = 9_001L;
    private static final long BOB_ID = 9_002L;
    private static final long CAROL_ID = 9_003L;
    private static final long ALICE_ARTICLE_ID = 9_101L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
        saveUser(ALICE_ID, "alice@test.local");
        saveUser(BOB_ID, "bob@test.local");

        Article article = new Article();
        article.setId(ALICE_ARTICLE_ID);
        article.setContent("Un article écrit par Alice.");
        article.setCategory(CategoryType.philosophie);
        article.setType(ArticleType.SAGESSE);
        article.setUserId(ALICE_ID);
        article.setLikes(0L);
        article.setComments(0L);
        article.setShares(0L);
        article.setDatePublication(LocalDateTime.now());
        articleRepository.save(article);
    }

    @Test
    @DisplayName("Publier : l'auteur est l'utilisateur connecté, pas le userId du body")
    void createArticleUsesAuthenticatedUser() throws Exception {
        String body = "{\"content\":\"Une pensée publiée par Bob.\",\"category\":\"philosophie\",\"tags\":[\"t\"],\"userId\":" + ALICE_ID + "}";

        mvc.perform(post("/articles/sagesse").with(as(BOB_ID))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated());

        List<Article> bobArticles = articleRepository.findByUserId(BOB_ID);
        assertEquals(1, bobArticles.size());
        assertEquals(1, articleRepository.findByUserId(ALICE_ID).size());
    }

    @Test
    @DisplayName("Supprimer : impossible de supprimer l'article d'un autre utilisateur")
    void cannotDeleteSomeoneElsesArticle() throws Exception {
        mvc.perform(delete("/articles/" + ALICE_ARTICLE_ID).with(as(BOB_ID)))
            .andExpect(status().isNotFound());

        assertTrue(articleRepository.existsById(ALICE_ARTICLE_ID));
    }

    @Test
    @DisplayName("Liker : le like est attribué à l'utilisateur connecté")
    void likeUsesAuthenticatedUser() throws Exception {
        mvc.perform(post("/articles/" + ALICE_ARTICLE_ID + "/like").param("currentUserId", String.valueOf(ALICE_ID))
                .with(as(BOB_ID)))
            .andExpect(status().isOk());

        assertTrue(userRepository.findById(BOB_ID).orElseThrow().getLikedArticlesId().contains(ALICE_ARTICLE_ID));
        assertFalse(userRepository.findById(ALICE_ID).orElseThrow().getLikedArticlesId().contains(ALICE_ARTICLE_ID));
    }

    @Test
    @DisplayName("Liker deux fois : le like n'est compté qu'une fois")
    void doubleLikeCountsOnce() throws Exception {
        mvc.perform(post("/articles/" + ALICE_ARTICLE_ID + "/like").with(as(BOB_ID))).andExpect(status().isOk());
        mvc.perform(post("/articles/" + ALICE_ARTICLE_ID + "/like").with(as(BOB_ID))).andExpect(status().isConflict());

        assertEquals(1L, articleRepository.findById(ALICE_ARTICLE_ID).orElseThrow().getLikes());
        assertEquals(1L, userRepository.findById(ALICE_ID).orElseThrow().getLikesReceived());
    }

    @Test
    @DisplayName("Profil : la mise à jour ne touche que le compte connecté")
    void updateProfileOnlyChangesCurrentUser() throws Exception {
        String body = "{\"id\":" + ALICE_ID + ",\"nom\":\"Pirate\",\"prenom\":\"Bob\",\"bio\":\"\"}";

        mvc.perform(put("/users/me").with(as(BOB_ID))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk());

        assertEquals("N", userRepository.findById(ALICE_ID).orElseThrow().getNom());
        assertEquals("Pirate", userRepository.findById(BOB_ID).orElseThrow().getNom());
    }

    @Test
    @DisplayName("Commentaires : supprimables par leur auteur ou par l'auteur de l'article, pas par un tiers")
    void commentDeletionRules() throws Exception {
        saveUser(CAROL_ID, "carol@test.local");
        long byBob = addComment(BOB_ID), byBob2 = addComment(BOB_ID), byCarol = addComment(CAROL_ID);

        mvc.perform(delete("/comments/" + byBob).with(as(CAROL_ID))).andExpect(status().isForbidden());
        mvc.perform(delete("/comments/" + byBob).with(as(BOB_ID))).andExpect(status().isOk());
        mvc.perform(delete("/comments/" + byBob2).with(as(ALICE_ID))).andExpect(status().isOk());
        mvc.perform(delete("/comments/" + byCarol).with(as(ALICE_ID))).andExpect(status().isOk());

        assertEquals(0L, articleRepository.findById(ALICE_ARTICLE_ID).orElseThrow().getComments());
    }

    private long addComment(long authorId) throws Exception {
        String json = mvc.perform(post("/comments/article/" + ALICE_ARTICLE_ID).with(as(authorId))
                .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Un commentaire\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new tools.jackson.databind.json.JsonMapper().readTree(json).get("comment").get("id").asLong();
    }

    @Test
    @DisplayName("Modération : réservée aux administrateurs")
    void usersCannotModerate() throws Exception {
        mvc.perform(put("/admin/signals/1/process").param("status", "APPROVED").with(as(BOB_ID)))
            .andExpect(status().isForbidden());
        mvc.perform(get("/admin/users").with(as(BOB_ID)))
            .andExpect(status().isForbidden());
        mvc.perform(get("/signals/pending").with(as(BOB_ID)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Vie privée : l'email d'un auteur n'est visible que par lui-même")
    void contactDetailsStayPrivate() throws Exception {
        mvc.perform(get("/articles/posts").with(as(BOB_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].user.id").value(ALICE_ID))
            .andExpect(jsonPath("$[0].user.email").doesNotExist());
        mvc.perform(get("/users/" + ALICE_ID).with(as(BOB_ID)))
            .andExpect(jsonPath("$.email").doesNotExist());
        mvc.perform(get("/users/" + ALICE_ID).with(as(ALICE_ID)))
            .andExpect(jsonPath("$.email").value("alice@test.local"));
    }

    @Test
    @DisplayName("CORS : seul le frontend configuré peut appeler l'API depuis un navigateur")
    void corsOnlyAllowsConfiguredOrigin() throws Exception {
        mvc.perform(options("/auth/login")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(options("/auth/login")
                .header("Origin", "https://evil.example")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isForbidden());
    }

    private RequestPostProcessor as(long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return user(new CustomUserDetails(u.getId(), u.getNom(), u.getPrenom(), null,
            u.getEmail(), u.getMotdepasse(), u.getAccountType()));
    }

    private void saveUser(long id, String email) {
        User user = new User();
        user.setId(id);
        user.setNom("N");
        user.setPrenom("P");
        user.setEmail(email);
        user.setMotdepasse("not-used");
        user.setAccountType(AccountType.USER);
        user.setContentCount(0L);
        user.setFollowersCount(0L);
        user.setFollowingCount(0L);
        user.setLikesReceived(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user.setReportedArticlesId(new HashSet<>());
        userRepository.save(user);
    }
}
