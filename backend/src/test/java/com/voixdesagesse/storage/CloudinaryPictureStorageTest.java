package com.voixdesagesse.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

@ExtendWith(MockitoExtension.class)
class CloudinaryPictureStorageTest {

    private static final String PREFIX = "https://res.cloudinary.com/demo-cloud/image/upload/";
    private static final String STORED = PREFIX + "v1729/voix-de-sagesse/profile-pictures/profile_7_1729.png";

    @Mock
    private Uploader uploader;

    private CloudinaryPictureStorage storage;

    @BeforeEach
    void setUp() {
        Cloudinary cloudinary = spy(new Cloudinary("cloudinary://key:secret@demo-cloud"));
        lenient().doReturn(uploader).when(cloudinary).uploader();
        storage = new CloudinaryPictureStorage(cloudinary);
    }

    @Test
    @DisplayName("Cloudinary - la photo est envoyée dans le dossier du projet, réduite à 512 px")
    void storeUploadsToProjectFolder() throws IOException {
        when(uploader.upload(any(), anyMap())).thenReturn(Map.of("secure_url", STORED));

        String url = storage.store(new MockMultipartFile("profilePicture", "photo.png", "image/png", new byte[] { 1 }), 7L);

        assertEquals(STORED, url);
        verify(uploader).upload(any(), argThat((Map<?, ?> options) -> "voix-de-sagesse/profile-pictures".equals(options.get("folder"))
                && "c_limit,h_512,w_512".equals(options.get("transformation"))));
    }

    @Test
    @DisplayName("Cloudinary - un fichier qui n'est pas une image n'est pas envoyé")
    void storeRejectsNonImage() {
        assertThrows(IOException.class,
                () -> storage.store(new MockMultipartFile("profilePicture", "evil.html", "text/html", new byte[] { 1 }), 7L));
        verifyNoInteractions(uploader);
    }

    @Test
    @DisplayName("Cloudinary - un refus du service devient une erreur d'entrée-sortie")
    void storeWrapsServiceErrors() throws IOException {
        when(uploader.upload(any(), anyMap())).thenThrow(new RuntimeException("Invalid image file"));

        assertThrows(IOException.class,
                () -> storage.store(new MockMultipartFile("profilePicture", "photo.png", "image/png", new byte[] { 1 }), 7L));
    }

    @Test
    @DisplayName("Cloudinary - la suppression vise l'image d'après son adresse")
    void deleteUsesPublicId() throws IOException {
        storage.delete(STORED);

        verify(uploader).destroy(eq("voix-de-sagesse/profile-pictures/profile_7_1729"), anyMap());
    }

    @Test
    @DisplayName("Cloudinary - les adresses d'un autre compte ou dossier sont ignorées")
    void deleteIgnoresForeignUrls() {
        storage.delete(null);
        storage.delete("/uploads/profile-pictures/profile_7_1.png");
        storage.delete("https://res.cloudinary.com/other-cloud/image/upload/v1/voix-de-sagesse/profile-pictures/profile_7_1.png");
        storage.delete(PREFIX + "v1/another-folder/picture.png");

        verifyNoInteractions(uploader);
    }

    @Test
    @DisplayName("Cloudinary - le démarrage échoue sans CLOUDINARY_URL")
    void requiresUrl() {
        assertThrows(IllegalStateException.class, () -> new CloudinaryPictureStorage(""));
    }
}
