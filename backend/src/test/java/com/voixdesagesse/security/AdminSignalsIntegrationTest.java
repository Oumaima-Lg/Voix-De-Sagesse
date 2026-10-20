package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.ArticleType;
import com.voixdesagesse.dto.CategoryType;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.repository.UserRepository;

import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSignalsIntegrationTest {

    private static final long AUTHOR_ID = 9_601L;
    private static final long BOB_ID = 9_602L;
    private static final long CAROL_ID = 9_603L;
    private static final long ADMIN_ID = 9_604L;
    private static final long ARTICLE_ID = 9_701L;
    private static final String CONTENT = "Un texte qui ne respecte pas les regles du site et que deux lecteurs ont signale.";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SignalRepository signalRepository;
    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        signalRepository.deleteAll();
        articleRepository.deleteById(ARTICLE_ID);
        userRepository.deleteAllById(List.of(AUTHOR_ID, BOB_ID, CAROL_ID, ADMIN_ID));
        saveUser(AUTHOR_ID, "author@test.local", AccountType.USER);
        saveUser(BOB_ID, "bob-signals@test.local", AccountType.USER);
        saveUser(CAROL_ID, "carol-signals@test.local", AccountType.USER);
        saveUser(ADMIN_ID, "admin-signals@test.local", AccountType.ADMIN);

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
    @DisplayName("Supprimer l'article : il disparaît, tous ses signalements sont traités, l'auteur est prévenu")
    void deletingSettlesAllReports() throws Exception {
        long first = report(BOB_ID);
        long second = report(CAROL_ID);

        process(first, "APPROVED").andExpect(status().isOk());

        assertFalse(articleRepository.existsById(ARTICLE_ID));
        assertEquals(SignalStatus.APPROVED, signalRepository.findById(second).orElseThrow().getStatus());
        ArgumentCaptor<MimeMessage> mail = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mail.capture());
        assertTrue(text(mail.getValue().getContent()).contains(CONTENT.substring(0, 40)));

        process(second, "APPROVED").andExpect(status().isConflict())
            .andExpect(jsonPath("$.errorMessage").value("Ce signalement a déjà été traité."));
    }

    @Test
    @DisplayName("Garder l'article : seul ce signalement est traité")
    void keepingTheArticle() throws Exception {
        long first = report(BOB_ID);
        long second = report(CAROL_ID);

        process(first, "REJECTED").andExpect(status().isOk());

        assertTrue(articleRepository.existsById(ARTICLE_ID));
        assertEquals(SignalStatus.PENDING, signalRepository.findById(second).orElseThrow().getStatus());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("L'auteur supprime son article : ses signalements en attente sont clos")
    void authorDeletionClosesReports() throws Exception {
        long id = report(BOB_ID);

        mvc.perform(delete("/articles/" + ARTICLE_ID).with(as(AUTHOR_ID, AccountType.USER))).andExpect(status().isOk());

        assertEquals(SignalStatus.CLOSED, signalRepository.findById(id).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Si l'email ne part pas, la modération est quand même enregistrée")
    void emailFailureDoesNotUndoModeration() throws Exception {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(MimeMessage.class));
        long id = report(BOB_ID);

        process(id, "APPROVED").andExpect(status().isOk());

        assertFalse(articleRepository.existsById(ARTICLE_ID));
        assertEquals(SignalStatus.APPROVED, signalRepository.findById(id).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Un ancien signalement dont l'article n'existe plus peut être traité")
    void reportOnMissingArticleCanBeSettled() throws Exception {
        long id = report(BOB_ID);
        articleRepository.deleteById(ARTICLE_ID);

        process(id, "APPROVED").andExpect(status().isOk());

        assertEquals(SignalStatus.APPROVED, signalRepository.findById(id).orElseThrow().getStatus());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Seules deux décisions existent : supprimer ou garder")
    void onlyDeleteOrKeep() throws Exception {
        long id = report(BOB_ID);
        process(id, "PENDING").andExpect(status().isBadRequest());
        process(id, "CLOSED").andExpect(status().isBadRequest());
    }

    private long report(long reporterId) throws Exception {
        String json = mvc.perform(post("/signals").with(as(reporterId, AccountType.USER)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"articleId\":" + ARTICLE_ID + ",\"reason\":\"Spam\",\"description\":\"\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new JsonMapper().readTree(json).get("id").asLong();
    }

    private ResultActions process(long signalId, String decision) throws Exception {
        return mvc.perform(put("/admin/signals/" + signalId + "/process").param("status", decision)
            .with(as(ADMIN_ID, AccountType.ADMIN)));
    }

    private static String text(Object content) throws Exception {
        if (content instanceof Multipart multipart) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++)
                text.append(text(multipart.getBodyPart(i).getContent()));
            return text.toString();
        }
        return String.valueOf(content);
    }

    private RequestPostProcessor as(long userId, AccountType type) {
        User u = userRepository.findById(userId).orElseThrow();
        return user(new CustomUserDetails(u.getId(), u.getNom(), u.getPrenom(), null, u.getEmail(), u.getMotdepasse(), type));
    }

    private void saveUser(long id, String email, AccountType type) {
        User user = new User();
        user.setId(id);
        user.setNom("N");
        user.setPrenom("P");
        user.setEmail(email);
        user.setMotdepasse("not-used");
        user.setAccountType(type);
        user.setContentCount(1L);
        user.setLikesReceived(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user.setReportedArticlesId(new HashSet<>());
        userRepository.save(user);
    }
}
