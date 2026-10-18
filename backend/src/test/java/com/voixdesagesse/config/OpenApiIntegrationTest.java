package com.voixdesagesse.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Swagger : la documentation est publique et décrit l'authentification JWT")
    void apiDocsArePublic() throws Exception {
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info.title").value("Voix De Sagesse API"))
            .andExpect(jsonPath("$.components.securitySchemes['bearer-jwt'].scheme").value("bearer"))
            .andExpect(jsonPath("$.paths['/auth/login'].post.summary").exists())
            .andExpect(jsonPath("$.paths['/articles/posts'].get").exists())
            // Login and sign-up need no token; the other routes do.
            .andExpect(jsonPath("$.paths['/auth/login'].post.security").isEmpty())
            .andExpect(jsonPath("$.paths['/articles/posts'].get.security").doesNotExist())
            .andExpect(jsonPath("$.paths['/error']").doesNotExist());
    }

    @Test
    @DisplayName("Swagger : l'interface est accessible sans être connecté")
    void swaggerUiIsPublic() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
