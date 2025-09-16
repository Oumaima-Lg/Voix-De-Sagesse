package com.voixdesagesse.VoixDeSagesse.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    private final UserService userService;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");


    private User getCurrentUser() throws ArticlaException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return userService.getUserByEmail(currentUserEmail).toEntity();
    }

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<?> followUser(@PathVariable Long targetUserId) throws ArticlaException {
            User currentUser = getCurrentUser();
            userService.followUser(currentUser.getId(), targetUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur suivi avec succès",
                    "success", true,
                    "isFollowing", true));
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long targetUserId) throws ArticlaException {
            User currentUser = getCurrentUser();
            userService.unfollowUser(currentUser.getId(), targetUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "Vous ne suivez plus cet utilisateur",
                    "success", true,
                    "isFollowing", false));

    }

    @GetMapping("/is-following/{targetUserId}")
    public ResponseEntity<?> isFollowingUser(@PathVariable Long targetUserId)  throws ArticlaException  {
            User currentUser = getCurrentUser();
            boolean isFollowing = userService.isFollowing(currentUser.getId(), targetUserId);

            return ResponseEntity.ok(Map.of(
                    "isFollowing", isFollowing,
                    "success", true));
    }

    @GetMapping("/following")
    public ResponseEntity<?> getFollowingUsers() throws ArticlaException {
        User currentUser = getCurrentUser();
        Set<Long> followingIds = currentUser.getFollowingId();
        List<User> followingUsers = userService.findAllFollowingUsersById(followingIds);

        List<UserProfileDTO> followingDTO = followingUsers.stream()
                .map(User::toProfileDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "following", followingDTO,
                "count", followingDTO.size(),
                "success", true));
    }

    @GetMapping("/my-followers")
    public ResponseEntity<?> getMyFollowers() throws ArticlaException {
        
        User currentUser = getCurrentUser();
        List<User> followers = userService.findByFollowingIdContaining(currentUser.getId());

        List<UserProfileDTO> followersDTO = followers.stream()
                .map(User::toProfileDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "followers", followersDTO,
                "count", followersDTO.size(),
                "success", true));

    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Valid @RequestBody UserProfileDTO profileDTO)
            throws ArticlaException {
        UserProfileDTO updatedUser = userService.updateUserProfile(profileDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable long userId) throws ArticlaException {
        return ResponseEntity.ok(userService.getUserProfileById(userId));
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
            @RequestParam("userId") Long userId, HttpServletRequest request) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Aucun fichier sélectionné"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Type de fichier non autorisé. Formats acceptés : JPEG, PNG, GIF, WebP"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La taille du fichier ne doit pas dépasser 5MB"));
            }

            String profilePictureUrl = userService.saveProfilePicture(file, userId);

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String fullUrl = baseUrl + profilePictureUrl;

            // Créer la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("profilePictureUrl", fullUrl); 
            response.put("message", "Image uploadée avec succès");
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'upload: " + e.getMessage());
            errorResponse.put("success", false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/uploads/profile-pictures/{filename}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(filename);

            // Sécurité : vérifier que le fichier est dans le bon répertoire
            if (!filePath.normalize().startsWith(uploadPath.normalize())) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/save-article/{articleId}")
    public ResponseEntity<?> saveArticle(@PathVariable Long articleId) throws ArticlaException {
            User currentUser = getCurrentUser();
            userService.saveArticle(currentUser.getId(), articleId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Article sauvegardé avec succès",
                "success", true,
                "isSaved", true
            )); 
    }

    @DeleteMapping("/unsave-article/{articleId}")
    public ResponseEntity<?> unsaveArticle(@PathVariable Long articleId) throws ArticlaException {
            User currentUser = getCurrentUser();
            userService.unsaveArticle(currentUser.getId(), articleId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Article retiré des favoris",
                "success", true,
                "isSaved", false
            ));
    }

    @GetMapping("/is-article-saved/{articleId}")
    public ResponseEntity<?> isArticleSaved(@PathVariable Long articleId) throws ArticlaException {
            User currentUser = getCurrentUser();
            boolean isSaved = userService.isArticleSaved(currentUser.getId(), articleId);
            
            return ResponseEntity.ok(Map.of(
                "isSaved", isSaved,
                "success", true
            ));
    }

    

}
