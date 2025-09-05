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

import org.springframework.beans.factory.annotation.Autowired;
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

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
public class UserController {

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    @Autowired
    private UserService userService;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    @PostMapping("/auth/register")
    public ResponseEntity<UserRegistrationDTO> registerUser(@RequestBody @Valid UserRegistrationDTO registerDTO)
            throws ArticlaException {

        return new ResponseEntity<>(userService.registerUser(registerDTO), HttpStatus.CREATED);
    }

    // @PostMapping("/auth/login")
    // public ResponseEntity<UserDTO> loginUser(@RequestBody @Valid LoginDTO
    // loginDTO) throws ArticlaException {

    // return new ResponseEntity<>(userService.loginUser(loginDTO), HttpStatus.OK);
    // }

    @PostMapping("/auth/changePass")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid LoginDTO loginDTO) throws ArticlaException {

        return new ResponseEntity<>(userService.changePassword(loginDTO), HttpStatus.OK);
    }

    @PostMapping("/sendOtp/{email}")
    public ResponseEntity<ResponseDTO> sendOtp(@PathVariable @Email(message = "{user.email.invalid}") String email)
            throws Exception {
        userService.sendOtp(email);
        return new ResponseEntity<>(new ResponseDTO("OTP sent successfully."), HttpStatus.OK);
    }

    @GetMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<ResponseDTO> verifyOtp(@PathVariable @Email(message = "{user.email.invalid}") String email,
            @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") @PathVariable String otp)
            throws ArticlaException {
        userService.verifyOtp(email, otp);
        return new ResponseEntity<>(new ResponseDTO("OTP has been verified."), HttpStatus.OK);
    }

    // ✅ Méthode utilitaire pour récupérer l'utilisateur connecté
    private User getCurrentUser() throws ArticlaException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return userService.getUserByEmail(currentUserEmail).toEntity();
    }

    // ✅ Endpoints simplifiés avec la méthode utilitaire
    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<?> followUser(@PathVariable Long targetUserId) throws ArticlaException {
        try {
            User currentUser = getCurrentUser();
            userService.followUser(currentUser.getId(), targetUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur suivi avec succès",
                    "success", true,
                    "isFollowing", true));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "success", false));
        }
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long targetUserId) throws ArticlaException {
        try {
            User currentUser = getCurrentUser();
            userService.unfollowUser(currentUser.getId(), targetUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "Vous ne suivez plus cet utilisateur",
                    "success", true,
                    "isFollowing", false));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "success", false));
        }
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
            // Récupérer l'utilisateur depuis le token JWT si nécessaire
            // String token = request.getHeader("Authorization");
            // Long authenticatedUserId = jwtService.getUserIdFromToken(token);

            // Valider le fichier
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Aucun fichier sélectionné"));
            }

            // Vérifier que c'est une image
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Type de fichier non autorisé. Formats acceptés : JPEG, PNG, GIF, WebP"));
            }

            // Vérifier la taille (5MB max)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La taille du fichier ne doit pas dépasser 5MB"));
            }

            // Appeler le service pour sauvegarder l'image
            String profilePictureUrl = userService.saveProfilePicture(file, userId);

            // Construire l'URL complète
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String fullUrl = baseUrl + profilePictureUrl;

            // Créer la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("profilePictureUrl", fullUrl); // URL complète
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

}
