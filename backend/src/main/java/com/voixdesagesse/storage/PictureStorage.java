package com.voixdesagesse.storage;

import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.exception.VoixDeSagesseException;

public interface PictureStorage {

    String store(MultipartFile file, Long userId) throws VoixDeSagesseException;

    void delete(String url);

    static String extensionOf(MultipartFile file) throws VoixDeSagesseException {
        return switch (String.valueOf(file.getContentType()).toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new VoixDeSagesseException("FILE_TYPE_NOT_ALLOWED");
        };
    }
}
