package com.voixdesagesse.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.security.CustomUserDetails;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ProfilePictureIntegrationTest {

    private static final long USER_ID = 9_701L;
    private static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 1, 2, 3 };

    @TempDir
    private static Path uploadDir;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void pictures(DynamicPropertyRegistry registry) {
        registry.add("app.pictures.upload-dir", () -> uploadDir.toString());
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteById(USER_ID);
        User user = new User();
        user.setId(USER_ID);
        user.setNom("Test");
        user.setPrenom("Photo");
        user.setEmail("picture@test.local");
        user.setMotdepasse("not-used");
        user.setAccountType(AccountType.USER);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    @DisplayName("Une photo envoyée est servie sans connexion")
    void uploadedPictureIsPublic() throws Exception {
        String url = upload();

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(PNG));
    }

    @Test
    @DisplayName("Une nouvelle photo supprime l'ancienne")
    void newPictureReplacesOldOne() throws Exception {
        String first = upload();
        String second = upload();

        assertNotEquals(first, second);
        mvc.perform(get(first)).andExpect(status().isNotFound());
        mvc.perform(get(second)).andExpect(status().isOk());
    }

    private String upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("profilePicture", "photo.png", "image/png", PNG);
        String body = mvc.perform(multipart("/users/me/picture").file(file).with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profilePictureUrl", startsWith("/uploads/profile-pictures/")))
                .andReturn().getResponse().getContentAsString();
        return new JsonMapper().readTree(body).get("profilePictureUrl").asString();
    }

    private RequestPostProcessor asUser() {
        return user(new CustomUserDetails(USER_ID, "Test", "Photo", null, "picture@test.local", "not-used",
                AccountType.USER));
    }
}
