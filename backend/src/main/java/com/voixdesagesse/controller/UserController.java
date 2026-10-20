package com.voixdesagesse.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.UserProfileDTO;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Utilisateurs")
@RestController
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    @Operation(summary = "Suivre un utilisateur")
    @PostMapping("/{targetUserId}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long targetUserId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        userService.followUser(currentUser.getId(), targetUserId);

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur suivi avec succès",
                "success", true,
                "isFollowing", true));
    }

    @Operation(summary = "Ne plus suivre un utilisateur")
    @DeleteMapping("/{targetUserId}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long targetUserId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        userService.unfollowUser(currentUser.getId(), targetUserId);

        return ResponseEntity.ok(Map.of(
                "message", "Vous ne suivez plus cet utilisateur",
                "success", true,
                "isFollowing", false));

    }

    @Operation(summary = "Savoir si l'on suit un utilisateur")
    @GetMapping("/{targetUserId}/follow")
    public ResponseEntity<?> isFollowingUser(@PathVariable Long targetUserId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        boolean isFollowing = userService.isFollowing(currentUser.getId(), targetUserId);

        return ResponseEntity.ok(Map.of(
                "isFollowing", isFollowing,
                "success", true));
    }

    @Operation(summary = "Utilisateurs que l'on suit")
    @GetMapping("/me/following")
    public ResponseEntity<?> getFollowingUsers() throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        Set<Long> followingIds = currentUser.getFollowingId();
        List<User> followingUsers = userService.findAllFollowingUsersById(followingIds);

        List<UserProfileDTO> followingDTO = followingUsers.stream()
                .map(User::toPublicProfileDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "following", followingDTO,
                "count", followingDTO.size(),
                "success", true));
    }

    @Operation(summary = "Ses abonnés")
    @GetMapping("/me/followers")
    public ResponseEntity<?> getMyFollowers() throws VoixDeSagesseException {

        User currentUser = userService.getCurrentUser();
        List<User> followers = userService.findByFollowingIdContaining(currentUser.getId());

        List<UserProfileDTO> followersDTO = followers.stream()
                .map(User::toPublicProfileDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "followers", followersDTO,
                "count", followersDTO.size(),
                "success", true));

    }

    @Operation(summary = "Modifier son profil")
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Valid @RequestBody UserProfileDTO profileDTO,
            @AuthenticationPrincipal CustomUserDetails currentUser)
            throws VoixDeSagesseException {
        UserProfileDTO updatedUser = userService.updateUserProfile(currentUser.getId(), profileDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Profil public d'un utilisateur")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userId == currentUser.getId() ? user.toProfileDTO() : user.toPublicProfileDTO());
    }

    @Operation(summary = "Changer sa photo de profil (image, 5 Mo max)")
    @PostMapping("/me/picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException, IOException {
        if (file.isEmpty())
            throw new VoixDeSagesseException("FILE_EMPTY");
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase()))
            throw new VoixDeSagesseException("FILE_TYPE_NOT_ALLOWED");

        String profilePictureUrl = userService.saveProfilePicture(file, currentUser.getId());
        return ResponseEntity.ok(Map.of(
                "profilePictureUrl", profilePictureUrl,
                "message", "Image uploadée avec succès",
                "success", true));
    }

    @Operation(summary = "Ajouter un article aux favoris")
    @PostMapping("/me/saved/{articleId}")
    public ResponseEntity<?> saveArticle(@PathVariable Long articleId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        userService.saveArticle(currentUser.getId(), articleId);

        return ResponseEntity.ok(Map.of(
                "message", "Article sauvegardé avec succès",
                "success", true,
                "isSaved", true));
    }

    @Operation(summary = "Retirer un article des favoris")
    @DeleteMapping("/me/saved/{articleId}")
    public ResponseEntity<?> unsaveArticle(@PathVariable Long articleId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        userService.unsaveArticle(currentUser.getId(), articleId);

        return ResponseEntity.ok(Map.of(
                "message", "Article retiré des favoris",
                "success", true,
                "isSaved", false));
    }

    @Operation(summary = "Savoir si un article est en favori")
    @GetMapping("/me/saved/{articleId}")
    public ResponseEntity<?> isArticleSaved(@PathVariable Long articleId) throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        boolean isSaved = userService.isArticleSaved(currentUser.getId(), articleId);

        return ResponseEntity.ok(Map.of(
                "isSaved", isSaved,
                "success", true));
    }

    @Operation(summary = "Supprimer son compte")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount() throws VoixDeSagesseException {
        User currentUser = userService.getCurrentUser();
        log.info("Demande de suppression de compte pour l'utilisateur: {}", currentUser.getId());
        userService.deleteUserAccount(currentUser.getId());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Votre compte a été supprimé avec succès"));
    }
}
