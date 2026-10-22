package com.voixdesagesse.exception;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.security.CustomUserDetails;

@SpringBootTest(properties = { "app.rate-limit.email-codes-per-hour=2", "app.client-ip-header=True-Client-IP" })
@AutoConfigureMockMvc
class FreePlanLimitsIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private ArticleRepository articleRepository;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Base pleine : on peut lire, mais publier renvoie un message clair (507)")
    void databaseFull() throws Exception {
        when(articleRepository.save(any(Article.class))).thenThrow(new UncategorizedMongoDbException("Write failed",
                new MongoException(8000, "you are over your space quota, using 513 MB of 512 MB")));

        mvc.perform(post("/articles/sagesse")
                .with(user(new CustomUserDetails(1L, "Test", "User", null, "user@test.local", "x", AccountType.USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"Une pensée assez longue pour être publiée.\",\"category\":\"philosophie\",\"tags\":[\"test\"]}"))
                .andExpect(status().isInsufficientStorage())
                .andExpect(jsonPath("$.errorMessage", containsString("limite de stockage")));
    }

    @Test
    @DisplayName("Base injoignable : la connexion renvoie un message clair (503)")
    void databaseUnavailable() throws Exception {
        when(userRepository.findByEmail(anyString())).thenThrow(new DataAccessResourceFailureException("Timed out",
                new MongoTimeoutException("Timed out after 30000 ms while waiting to connect")));

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"someone@test.local\",\"password\":\"Secret123!\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorMessage").value("La base de données ne répond pas pour le moment. Réessayez dans quelques minutes."));
    }

    @Test
    @DisplayName("Codes par email : refusés au-delà de la limite pour un même visiteur, pas pour les autres (429)")
    void codeRequestsAreLimitedPerVisitor() throws Exception {
        for (int i = 0; i < 2; i++)
            requestCode("203.0.113.7").andExpect(status().isOk());

        requestCode("203.0.113.7")
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorMessage").value("Trop de demandes de code depuis votre connexion. Réessayez dans une heure."));
        requestCode("198.51.100.2").andExpect(status().isOk());
    }

    private ResultActions requestCode(String clientIp) throws Exception {
        return mvc.perform(post("/auth/password/otp").header("True-Client-IP", clientIp)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"nobody@test.local\"}"));
    }
}
