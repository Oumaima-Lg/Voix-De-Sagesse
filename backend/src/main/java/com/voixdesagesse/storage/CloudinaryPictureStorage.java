package com.voixdesagesse.storage;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "app.pictures.storage", havingValue = "cloudinary")
@Slf4j
public class CloudinaryPictureStorage implements PictureStorage {

    private static final String FOLDER = "voix-de-sagesse/profile-pictures";
    private static final Pattern PUBLIC_ID = Pattern.compile("^(?:v\\d+/)?(" + FOLDER + "/[\\w-]+)\\.\\w+$");

    private final Cloudinary cloudinary;
    private final String urlPrefix;

    @Autowired
    public CloudinaryPictureStorage(@Value("${app.pictures.cloudinary-url}") String cloudinaryUrl) {
        this(new Cloudinary(requireUrl(cloudinaryUrl)));
    }

    CloudinaryPictureStorage(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        this.urlPrefix = "https://res.cloudinary.com/" + cloudinary.config.cloudName + "/image/upload/";
    }

    private static String requireUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank())
            throw new IllegalStateException("CLOUDINARY_URL is required when PICTURE_STORAGE is cloudinary");
        return cloudinaryUrl;
    }

    @Override
    public String store(MultipartFile file, Long userId) throws IOException {
        PictureStorage.extensionOf(file);
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", FOLDER,
                    "public_id", "profile_" + userId + "_" + System.currentTimeMillis(),
                    "resource_type", "image",
                    "transformation", "c_limit,h_512,w_512"));
            return (String) result.get("secure_url");
        } catch (RuntimeException e) {
            throw new IOException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || !url.startsWith(urlPrefix))
            return;
        Matcher matcher = PUBLIC_ID.matcher(url.substring(urlPrefix.length()));
        if (!matcher.matches())
            return;
        try {
            cloudinary.uploader().destroy(matcher.group(1), ObjectUtils.asMap("invalidate", true));
        } catch (IOException | RuntimeException e) {
            log.warn("Could not delete profile picture {}: {}", matcher.group(1), e.getMessage());
        }
    }
}
