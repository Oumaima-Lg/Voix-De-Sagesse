package com.voixdesagesse.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.voixdesagesse.exception.VoixDeSagesseException;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "app.pictures.storage", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalPictureStorage implements PictureStorage, WebMvcConfigurer {

    public static final String URL_PREFIX = "/uploads/profile-pictures/";

    private final Path directory;

    public LocalPictureStorage(@Value("${app.pictures.upload-dir}") String uploadDir) {
        this.directory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, Long userId) throws VoixDeSagesseException {
        String name = "profile_" + userId + "_" + System.currentTimeMillis() + PictureStorage.extensionOf(file);
        try {
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), directory.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Could not save profile picture {}", name, e);
            throw new VoixDeSagesseException("PICTURE_STORAGE_UNAVAILABLE");
        }
        return URL_PREFIX + name;
    }

    @Override
    public void delete(String url) {
        if (url == null || !url.startsWith(URL_PREFIX))
            return;
        Path file = directory.resolve(url.substring(URL_PREFIX.length())).normalize();
        if (!directory.equals(file.getParent()))
            return;
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Could not delete profile picture {}: {}", file.getFileName(), e.getMessage());
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(URL_PREFIX + "**")
                .addResourceLocations("file:" + directory + "/")
                .setCachePeriod(3600);
    }
}
