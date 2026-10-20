package com.voixdesagesse.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.CommentRepository;
import com.voixdesagesse.repository.NotificationRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.service.ArticleService;

@SpringBootTest(properties = { "app.demo-data.enabled=true", "app.demo-data.user-password=Demo1234!",
        "app.demo-data.admin-password=Admin1234!" })
class DemoDataLoaderIntegrationTest {

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
        User demo = userRepository.findByEmail(DemoDataLoader.DEMO_EMAIL).orElseThrow();
        assertTrue(passwordEncoder.matches("Demo1234!", demo.getMotdepasse()));
        assertEquals(AccountType.ADMIN, userRepository.findByEmail(DemoDataLoader.ADMIN_EMAIL).orElseThrow().getAccountType());

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
}
