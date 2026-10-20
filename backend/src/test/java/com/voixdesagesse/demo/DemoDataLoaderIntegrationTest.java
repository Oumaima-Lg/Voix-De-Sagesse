package com.voixdesagesse.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.CommentRepository;
import com.voixdesagesse.repository.NotificationRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.ArticleService;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(properties = { "app.demo-data.enabled=true", "app.demo-data.user-password=Demo1234!",
        "app.demo-data.admin-password=Admin1234!" })
@AutoConfigureMockMvc
class DemoDataLoaderIntegrationTest {

    private static final String PROTECTED = "Cette action est désactivée sur les comptes de démonstration, partagés par tous les visiteurs.";

    @Autowired
    private DemoDataLoader loader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SignalRepository signalRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void emptyDatabase() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
        commentRepository.deleteAll();
        notificationRepository.deleteAll();
        signalRepository.deleteAll();
    }

    @Test
    @DisplayName("Démo : comptes, articles et réactions cohérents, avec des notifications pour le compte démo")
    void loadsConsistentDemoData() throws Exception {
        loader.run(null);

        assertEquals(5, userRepository.count());
        assertEquals(9, articleRepository.count());
        User demo = userRepository.findByEmail(DemoAccounts.DEMO_EMAIL).orElseThrow();
        assertTrue(passwordEncoder.matches("Demo1234!", demo.getMotdepasse()));
        assertEquals(AccountType.ADMIN, userRepository.findByEmail(DemoAccounts.ADMIN_EMAIL).orElseThrow().getAccountType());

        assertEquals(2L, demo.getContentCount());
        assertEquals(2L, demo.getFollowersCount());
        long likesOnDemoArticles = articleRepository.findByUserId(demo.getId()).stream().mapToLong(Article::getLikes).sum();
        assertEquals(likesOnDemoArticles, demo.getLikesReceived());
        assertTrue(notificationRepository.countByRecipientIdAndReadFalse(demo.getId()) >= 5);

        List<LocalDateTime> dates = articleService.displayPosts(demo.getId()).stream()
            .map(post -> post.getArticle().getDatePublication()).toList();
        assertEquals(dates.stream().sorted(Comparator.reverseOrder()).toList(), dates);
    }

    @Test
    @DisplayName("Démo : rien n'est ajouté quand la base contient déjà des comptes")
    void skipsWhenUsersExist() throws Exception {
        loader.run(null);
        loader.run(null);
        assertEquals(5, userRepository.count());
        assertEquals(9, articleRepository.count());
    }

    @Test
    @DisplayName("Démo : les comptes partagés gardent leur mot de passe, leur profil, leur compte et leurs articles")
    void sharedAccountsAreProtected() throws Exception {
        loader.run(null);
        User demo = userRepository.findByEmail(DemoAccounts.DEMO_EMAIL).orElseThrow();
        long demoArticle = articleRepository.findByUserId(demo.getId()).get(0).getId();
        MockMultipartFile picture = new MockMultipartFile("profilePicture", "photo.png", "image/png", new byte[] { 1, 2, 3 });

        expectProtected(post("/auth/password/otp").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + DemoAccounts.DEMO_EMAIL + "\"}"));
        expectProtected(delete("/users/me").with(as(demo)));
        expectProtected(put("/users/me").with(as(demo)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Autre\",\"prenom\":\"Nom\"}"));
        expectProtected(multipart("/users/me/picture").file(picture).with(as(demo)));
        expectProtected(delete("/articles/" + demoArticle).with(as(demo)));

        User after = userRepository.findByEmail(DemoAccounts.DEMO_EMAIL).orElseThrow();
        assertEquals("Démo", after.getNom());
        assertNull(after.getProfilePicture());
        assertEquals(2, articleRepository.findByUserId(demo.getId()).size());
    }

    @Test
    @DisplayName("Démo : un visiteur peut supprimer ce qu'il publie, et l'admin peut retirer un article démo signalé")
    void visitorPostsAndModerationStillWork() throws Exception {
        loader.run(null);
        User demo = userRepository.findByEmail(DemoAccounts.DEMO_EMAIL).orElseThrow();
        User karim = userRepository.findByEmail(DemoAccounts.KARIM_EMAIL).orElseThrow();
        User admin = userRepository.findByEmail(DemoAccounts.ADMIN_EMAIL).orElseThrow();

        String created = mvc.perform(post("/articles/sagesse").with(as(demo)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"Une pensée publiée par un visiteur pour essayer.\",\"category\":\"philosophie\",\"tags\":[\"test\"]}"))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        long visitorArticle = new JsonMapper().readTree(created).get("id").asLong();
        mvc.perform(delete("/articles/" + visitorArticle).with(as(demo))).andExpect(status().isOk());

        long demoArticle = articleRepository.findByUserId(demo.getId()).get(0).getId();
        String signal = mvc.perform(post("/signals").with(as(karim)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"articleId\":" + demoArticle + ",\"reason\":\"Spam\",\"description\":\"\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        mvc.perform(put("/admin/signals/" + new JsonMapper().readTree(signal).get("id").asLong() + "/process")
                .param("status", "APPROVED").with(as(admin)))
                .andExpect(status().isOk());

        assertTrue(articleRepository.findById(demoArticle).isEmpty());
    }

    private void expectProtected(RequestBuilder request) throws Exception {
        mvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value(PROTECTED));
    }

    private static RequestPostProcessor as(User user) {
        return user(new CustomUserDetails(user.getId(), user.getNom(), user.getPrenom(), null, user.getEmail(),
                user.getMotdepasse(), user.getAccountType()));
    }
}
