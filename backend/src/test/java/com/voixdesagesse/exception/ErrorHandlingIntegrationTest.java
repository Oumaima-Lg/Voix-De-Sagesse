package com.voixdesagesse.exception;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.security.CustomUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlingIntegrationTest {

    private static final long USER_ID = 9_201L;
    private static final long UNKNOWN_ID = 987_654L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(USER_ID);
        user.setNom("N");
        user.setPrenom("P");
        user.setEmail("errors@test.local");
        user.setMotdepasse("not-used");
        user.setAccountType(AccountType.USER);
        user.setLikedArticlesId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    @DisplayName("Erreurs métier : toujours { errorMessage, errorCode, timeStamp } avec le bon code HTTP")
    void businessErrorsShareOneFormat() throws Exception {
        expectError(mvc.perform(post("/comments/article/" + UNKNOWN_ID).with(asUser())
                .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Bonjour\"}")),
            404, "Article introuvable.");
        expectError(mvc.perform(delete("/comments/" + UNKNOWN_ID).with(asUser())), 404, "Commentaire introuvable.");
        expectError(mvc.perform(post("/articles/" + UNKNOWN_ID + "/like").with(asUser())), 404, "Article introuvable.");
        expectError(mvc.perform(post("/users/" + USER_ID + "/follow").with(asUser())), 400,
            "Vous ne pouvez pas vous abonner à vous-même.");
    }

    @Test
    @DisplayName("Validation : le message du champ, sans nom de champ technique")
    void validationErrorsAreReadable() throws Exception {
        expectError(mvc.perform(post("/articles/sagesse").with(asUser()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"Court\",\"category\":\"philosophie\",\"tags\":[\"t\"]}")),
            400, "Le contenu doit contenir entre 10 et 1000 caractères");
    }

    @Test
    @DisplayName("Photo de profil : un fichier qui n'est pas une image est refusé avec un message clair")
    void uploadRejectsNonImages() throws Exception {
        MockMultipartFile text = new MockMultipartFile("profilePicture", "notes.txt", "text/plain", "x".getBytes());
        expectError(mvc.perform(multipart("/users/me/picture").file(text).with(asUser())), 400,
            "Format non accepté. Formats acceptés : JPEG, PNG, GIF, WebP.");
    }

    private void expectError(ResultActions result, int status, String message) throws Exception {
        result.andExpect(status().is(status))
            .andExpect(jsonPath("$.errorMessage").value(message))
            .andExpect(jsonPath("$.errorCode").value(status))
            .andExpect(jsonPath("$.timeStamp").exists());
    }

    private RequestPostProcessor asUser() {
        return user(new CustomUserDetails(USER_ID, "N", "P", null, "errors@test.local", "not-used", AccountType.USER));
    }

    @Test
    @DisplayName("JSON invalide : 400")
    void unreadableJsonIsBadRequest() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value("Requête invalide."));
    }

    @Test
    @DisplayName("Route inconnue : 404")
    void unknownRouteIsNotFound() throws Exception {
        mvc.perform(get("/does-not-exist").with(user(new CustomUserDetails(1L, "N", "P", null,
                "someone@test.local", "x", AccountType.USER))))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Sans authentification : 401 au format JSON")
    void unauthenticatedIsJson401() throws Exception {
        mvc.perform(get("/articles/posts"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorMessage").value("Authentification requise."));
    }

    @Test
    @DisplayName("Health check public pour l'hébergeur")
    void healthIsPublic() throws Exception {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Connexion : 401 puis 429 après 5 échecs")
    void loginIsBlockedAfterFiveFailures() throws Exception {
        String body = "{\"email\":\"brute-force@test.local\",\"password\":\"wrong\"}";
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value("Email ou mot de passe incorrect."));
        }
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isTooManyRequests());
    }
}
