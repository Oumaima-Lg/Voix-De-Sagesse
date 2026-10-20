package com.voixdesagesse.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface PictureStorage {

    String store(MultipartFile file, Long userId) throws IOException;

    void delete(String url);

    static String extensionOf(MultipartFile file) throws IOException {
        return switch (String.valueOf(file.getContentType()).toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new IOException("Unsupported image type");
        };
    }
}
