package com.voixdesagesse.VoixDeSagesse.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Impossible de créer le répertoire d'upload", e);
            }
        }
        
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:" + uploadPath.toAbsolutePath() + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }

}
