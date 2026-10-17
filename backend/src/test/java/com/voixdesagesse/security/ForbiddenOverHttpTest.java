package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.UserRepository;

/**
 * Over a real HTTP server, a forbidden request goes through the /error dispatch, which MockMvc does not
 * simulate: a logged-in user calling an admin endpoint must get 403, not 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ForbiddenOverHttpTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Un utilisateur connecté reçoit 403 sur une route admin")
    void userGetsForbiddenOnAdminRoute() throws Exception {
        User user = new User();
        user.setId(9_501L);
        user.setNom("N");
        user.setPrenom("P");
        user.setEmail("forbidden@test.local");
        user.setMotdepasse(passwordEncoder.encode("Secret123!"));
        user.setAccountType(AccountType.USER);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        userRepository.save(user);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> login = client.send(HttpRequest.newBuilder(uri("/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"forbidden@test.local\",\"password\":\"Secret123!\"}"))
                .build(), HttpResponse.BodyHandlers.ofString());
        String jwt = objectMapper.readTree(login.body()).get("jwt").asText();

        HttpResponse<String> admin = client.send(HttpRequest.newBuilder(uri("/admin/users"))
                .header("Authorization", "Bearer " + jwt).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(403, admin.statusCode());
        assertEquals("Accès refusé.", objectMapper.readTree(admin.body()).get("errorMessage").asText());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
