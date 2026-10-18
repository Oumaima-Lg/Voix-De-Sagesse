package com.voixdesagesse.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

/** Swagger UI at /swagger-ui.html. Routes need the JWT unless their controller says otherwise. */
@Configuration
public class OpenApiConfig {

    public static final String BEARER_JWT = "bearer-jwt";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voix De Sagesse API")
                        .version("1.0")
                        .description("API REST de Voix De Sagesse. Pour tester les routes protégées : "
                                + "appelez POST /auth/login, copiez le jeton reçu, puis cliquez sur « Authorize »."))
                .components(new Components().addSecuritySchemes(BEARER_JWT,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_JWT))
                // Sections in reading order; each controller joins its section with @Tag(name = ...).
                .tags(List.of(
                        new Tag().name("Authentification").description("Connexion, inscription et mot de passe oublié. Routes publiques."),
                        new Tag().name("Articles").description("Publier, lire, aimer et rechercher des articles."),
                        new Tag().name("Commentaires").description("Commenter les articles."),
                        new Tag().name("Utilisateurs").description("Profil, abonnements et favoris."),
                        new Tag().name("Notifications").description("Réactions des autres utilisateurs et décisions de modération."),
                        new Tag().name("Signalements").description("Signaler un article inapproprié."),
                        new Tag().name("Administration").description("Réservé aux comptes ADMIN.")));
    }
}
