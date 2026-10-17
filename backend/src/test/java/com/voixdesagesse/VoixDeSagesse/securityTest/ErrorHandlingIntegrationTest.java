package com.voixdesagesse.VoixDeSagesse.securityTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;
import com.voixdesagesse.VoixDeSagesse.jwt.CustomUserDetails;

/** Errors use proper HTTP statuses and readable messages, without internal details. */
@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlingIntegrationTest {

    @Autowired
    private MockMvc mvc;

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
