package com.voixdesagesse.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class LocalPictureStorageTest {

    @TempDir
    private Path root;

    private Path directory;
    private LocalPictureStorage storage;

    @BeforeEach
    void setUp() {
        directory = root.resolve("profile-pictures");
        storage = new LocalPictureStorage(directory.toString());
    }

    @Test
    @DisplayName("Photo locale - le nom vient du type d'image, pas du nom envoyé")
    void storeUsesImageType() throws IOException {
        String url = storage.store(new MockMultipartFile("profilePicture", "evil.html", "image/png", new byte[] { 1, 2, 3 }), 7L);

        assertTrue(url.startsWith("/uploads/profile-pictures/profile_7_"), url);
        assertTrue(url.endsWith(".png"), url);
        assertArrayEquals(new byte[] { 1, 2, 3 }, Files.readAllBytes(fileOf(url)));
    }

    @Test
    @DisplayName("Photo locale - un fichier qui n'est pas une image est refusé")
    void storeRejectsNonImage() {
        assertThrows(IOException.class,
                () -> storage.store(new MockMultipartFile("profilePicture", "evil.html", "text/html", new byte[] { 1 }), 7L));
        assertFalse(Files.exists(directory));
    }

    @Test
    @DisplayName("Photo locale - la suppression retire le fichier")
    void deleteRemovesFile() throws IOException {
        String url = storage.store(new MockMultipartFile("profilePicture", "a.png", "image/png", new byte[] { 1 }), 7L);

        storage.delete(url);

        assertFalse(Files.exists(fileOf(url)));
    }

    @Test
    @DisplayName("Photo locale - rien n'est supprimé en dehors du dossier des photos")
    void deleteStaysInsideDirectory() throws IOException {
        Path secret = Files.writeString(root.resolve("secret.txt"), "garder");

        storage.delete(null);
        storage.delete("https://res.cloudinary.com/demo/image/upload/v1/secret.png");
        storage.delete("/uploads/profile-pictures/../secret.txt");

        assertTrue(Files.exists(secret));
    }

    private Path fileOf(String url) {
        return directory.resolve(url.substring(LocalPictureStorage.URL_PREFIX.length()));
    }
}
