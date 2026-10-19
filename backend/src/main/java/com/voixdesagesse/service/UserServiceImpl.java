package com.voixdesagesse.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.NotificationType;
import com.voixdesagesse.dto.ResetPasswordRequest;
import com.voixdesagesse.dto.ResponseDTO;
import com.voixdesagesse.dto.UserDTO;
import com.voixdesagesse.dto.UserProfileDTO;
import com.voixdesagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.mail.EmailTemplates;
import com.voixdesagesse.mail.MailService;
import com.voixdesagesse.entity.OTP;
import com.voixdesagesse.entity.PendingRegistration;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.OTPRepository;
import com.voixdesagesse.repository.PendingRegistrationRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "userService")
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp");

    private static final long OTP_VALIDITY_MINUTES = 10;
    private static final int OTP_MAX_ATTEMPTS = 5;
    private static final long OTP_RESEND_DELAY_SECONDS = 60;

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final NotificationService notificationService;
    private final CommonService commonService;
    private final SignalRepository signalRepository;
    private final ArticleRepository articleRepository;

    @Override
    public User getCurrentUser() throws VoixDeSagesseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return getUserByEmail(currentUserEmail).toEntity();
    }

    @Override
    public ResponseDTO startRegistration(UserRegistrationDTO userDTO) throws VoixDeSagesseException {
        String email = userDTO.getEmail();
        if (userRepository.findByEmail(email).isPresent())
            throw new VoixDeSagesseException("USER_FOUND");

        Optional<PendingRegistration> previous = pendingRegistrationRepository.findById(email);
        if (previous.isPresent()
                && previous.get().getCreationTime().isAfter(LocalDateTime.now().minusSeconds(OTP_RESEND_DELAY_SECONDS)))
            throw new VoixDeSagesseException("OTP_TOO_SOON");

        // Signing up again replaces the previous pending sign-up (new code, new details).
        String code = Utilities.generateOTP();
        PendingRegistration pending = new PendingRegistration(email, userDTO.getNom(), userDTO.getPrenom(),
                passwordEncoder.encode(userDTO.getMotdepasse()), code, LocalDateTime.now(), 0);
        pendingRegistrationRepository.save(pending);
        try {
            mailService.sendHtml(email, "Confirmez votre inscription - Voix De Sagesse",
                    EmailTemplates.registrationCode(code, pending.getNom(), pending.getPrenom()));
        } catch (VoixDeSagesseException e) {
            // Without the email the code is useless: let the user try again right away.
            pendingRegistrationRepository.delete(pending);
            throw e;
        }
        return new ResponseDTO("Un code de vérification a été envoyé à votre adresse email.");
    }

    @Override
    public UserRegistrationDTO confirmRegistration(String email, String otp) throws VoixDeSagesseException {
        PendingRegistration pending = pendingRegistrationRepository.findById(email)
                .filter(p -> !isExpired(p.getCreationTime()))
                .orElseThrow(() -> new VoixDeSagesseException("OTP_NOT_FOUND"));

        if (!pending.getOtpCode().equals(otp)) {
            pending.setAttempts(pending.getAttempts() + 1);
            if (pending.getAttempts() >= OTP_MAX_ATTEMPTS) {
                pendingRegistrationRepository.delete(pending);
                throw new VoixDeSagesseException("OTP_TOO_MANY_ATTEMPTS");
            }
            pendingRegistrationRepository.save(pending);
            throw new VoixDeSagesseException("OTP_INCORRECT");
        }

        pendingRegistrationRepository.delete(pending);
        return saveNewUser(pending.getNom(), pending.getPrenom(), email, pending.getMotdepasse(), AccountType.USER)
                .toRegisterDTO();
    }

    @Override
    public User createAccount(String nom, String prenom, String email, String password, AccountType accountType)
            throws VoixDeSagesseException {
        return saveNewUser(nom, prenom, email, passwordEncoder.encode(password), accountType);
    }

    // The password is already hashed.
    private User saveNewUser(String nom, String prenom, String email, String encodedPassword, AccountType accountType)
            throws VoixDeSagesseException {
        // The address may have been registered by another sign-up in the meantime.
        if (userRepository.findByEmail(email).isPresent())
            throw new VoixDeSagesseException("USER_FOUND");

        User user = new User();
        user.setId(Utilities.getNextSequence("users"));
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotdepasse(encodedPassword);
        user.setAccountType(accountType);
        user.setUsername(prenom);
        user.setFollowersCount(0L);
        user.setContentCount(0L);
        user.setFollowingCount(0L);
        user.setLikesReceived(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user.setReportedArticlesId(new HashSet<>());
        user.setDateInscription(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Boolean sendOtp(String email) throws VoixDeSagesseException {
        Optional<User> found = userRepository.findByEmail(email);
        if (found.isEmpty()) {
            // Same answer as for a known address, so this endpoint cannot be used to find accounts.
            log.info("OTP requested for an unknown email");
            return true;
        }
        User user = found.get();
        Optional<OTP> previous = otpRepository.findById(email);
        if (previous.isPresent()
                && previous.get().getCreationTime().isAfter(LocalDateTime.now().minusSeconds(OTP_RESEND_DELAY_SECONDS)))
            throw new VoixDeSagesseException("OTP_TOO_SOON");

        String genOtp = Utilities.generateOTP();
        OTP otp = new OTP(email, genOtp, LocalDateTime.now());
        otpRepository.save(otp);
        try {
            mailService.sendHtml(email, "Votre code de vérification - Voix De Sagesse",
                    EmailTemplates.otpCode(genOtp, user.getNom(), user.getPrenom()));
        } catch (VoixDeSagesseException e) {
            otpRepository.delete(otp);
            throw e;
        }
        return true;
    }

    @Override
    public String verifyOtp(String email, String otp) throws VoixDeSagesseException {
        OTP otpEntity = otpRepository.findById(email).orElseThrow(() -> new VoixDeSagesseException("OTP_NOT_FOUND"));
        if (isExpired(otpEntity.getCreationTime()) || otpEntity.getOtpCode() == null)
            throw new VoixDeSagesseException("OTP_NOT_FOUND");

        if (!otpEntity.getOtpCode().equals(otp)) {
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            if (otpEntity.getAttempts() >= OTP_MAX_ATTEMPTS) {
                otpRepository.delete(otpEntity);
                throw new VoixDeSagesseException("OTP_TOO_MANY_ATTEMPTS");
            }
            otpRepository.save(otpEntity);
            throw new VoixDeSagesseException("OTP_INCORRECT");
        }

        String resetToken = Utilities.generateResetToken();
        otpEntity.setOtpCode(null);
        otpEntity.setResetToken(resetToken);
        otpRepository.save(otpEntity);
        return resetToken;
    }

    @Override
    public ResponseDTO changePassword(ResetPasswordRequest request) throws VoixDeSagesseException {
        OTP otpEntity = otpRepository.findById(request.email())
                .filter(otp -> otp.getResetToken() != null && !isExpired(otp.getCreationTime()))
                .filter(otp -> MessageDigest.isEqual(
                        otp.getResetToken().getBytes(StandardCharsets.UTF_8),
                        request.resetToken().getBytes(StandardCharsets.UTF_8)))
                .orElseThrow(() -> new VoixDeSagesseException("RESET_TOKEN_INVALID"));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new VoixDeSagesseException("USER_NOT_FOUND"));
        user.setMotdepasse(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        otpRepository.delete(otpEntity);
        return new ResponseDTO("Mot de passe modifié avec succès.");
    }

    private boolean isExpired(LocalDateTime creationTime) {
        return creationTime.isBefore(LocalDateTime.now().minusMinutes(OTP_VALIDITY_MINUTES));
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTPs() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(OTP_VALIDITY_MINUTES);
        List<OTP> expiredOTPs = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOTPs.isEmpty()) {
            otpRepository.deleteAll(expiredOTPs);
        }
        List<PendingRegistration> expiredSignUps = pendingRegistrationRepository.findByCreationTimeBefore(expiry);
        if (!expiredSignUps.isEmpty()) {
            pendingRegistrationRepository.deleteAll(expiredSignUps);
        }
    }

    @Override
    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO profileDTO) throws VoixDeSagesseException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VoixDeSagesseException("USER_NOT_FOUND"));
        // Email, picture and counters are not editable here: the picture only changes through the upload endpoint.
        user.setNom(profileDTO.getNom());
        user.setPrenom(profileDTO.getPrenom());
        user.setBio(profileDTO.getBio());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        user.setLocation(profileDTO.getLocation());
        user.setWebsite(profileDTO.getWebsite());
        return userRepository.save(user).toProfileDTO();
    }

    @Override
    public User getUserById(long userId) throws VoixDeSagesseException {
        return userRepository.findById(userId).orElseThrow(() -> new VoixDeSagesseException("USER_NOT_FOUND"));
    }

    @Override
    public boolean addLikedArticle(Long userId, Long articleId) {
        return userRepository.addLikedArticle(userId, articleId) > 0;
    }

    @Override
    public boolean removeLikedArticle(Long userId, Long articleId) {
        return userRepository.removeLikedArticle(userId, articleId) > 0;
    }

    @Override
    public void incrementLikesReceived(Long userId) {
        userRepository.incrementLikesReceived(userId);
    }

    @Override
    public void decrementLikesReceived(Long userId) {
        userRepository.decrementLikesReceived(userId);
    }

    @Override
    public void incrementContentCount(Long userId) {
        userRepository.incrementContentCount(userId);
    }

    @Override
    public void decrementContentCount(Long userId) {
        userRepository.decrementContentCount(userId);
    }

    @Override
    public void followUser(Long currentUserId, Long targetUserId) throws VoixDeSagesseException {

        if (currentUserId.equals(targetUserId))
            throw new VoixDeSagesseException("SELF_FOLLOW");

        if (!userRepository.existsById(currentUserId))
            throw new VoixDeSagesseException("USER_NOT_FOUND");

        if (!userRepository.existsById(targetUserId))
            throw new VoixDeSagesseException("USER_NOT_FOUND");

        if (isFollowing(currentUserId, targetUserId))
            throw new VoixDeSagesseException("ALREADY_FOLLOWING");

        addFollowing(currentUserId, targetUserId);
        incrementFollowingCount(currentUserId);
        incrementFollowersCount(targetUserId);
        notificationService.userFollowed(targetUserId, currentUserId);
    }

    @Override
    public void unfollowUser(Long currentUserId, Long targetUserId) throws VoixDeSagesseException {
        if (currentUserId.equals(targetUserId))
            throw new VoixDeSagesseException("SELF_FOLLOW");

        if (!isFollowing(currentUserId, targetUserId))
            throw new VoixDeSagesseException("NOT_FOLLOWING");

        removeFollowing(currentUserId, targetUserId);
        decrementFollowingCount(currentUserId);
        decrementFollowersCount(targetUserId);
        notificationService.withdraw(NotificationType.FOLLOW, targetUserId, currentUserId, null);
    }

    @Override
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        return userRepository.isFollowing(currentUserId, targetUserId);
    }

    @Override
    public void addFollowing(Long currentUserId, Long targetUserId) {
        userRepository.addFollowing(currentUserId, targetUserId);
    }

    @Override
    public void removeFollowing(Long currentUserId, Long targetUserId) {
        userRepository.removeFollowing(currentUserId, targetUserId);
    }

    @Override
    public void incrementFollowingCount(Long userId) {
        userRepository.incrementFollowingCount(userId);
    }

    @Override
    public void decrementFollowingCount(Long userId) {
        userRepository.decrementFollowingCount(userId);
    }

    @Override
    public void incrementFollowersCount(Long userId) {
        userRepository.incrementFollowersCount(userId);
    }

    @Override
    public void decrementFollowersCount(Long userId) {
        userRepository.decrementFollowersCount(userId);
    }

    @Override
    public List<User> findAllFollowingUsersById(Set<Long> followingIds) {
        List<User> followingUsers = new ArrayList<>();

        if (followingIds != null && !followingIds.isEmpty()) {
            followingUsers = userRepository.findAllById(followingIds);
        }

        return followingUsers;
    }

    @Override
    public List<User> findByFollowingIdContaining(Long userId) {
        return userRepository.findByFollowingIdContaining(userId);
    }

    @Override
    public UserDTO getUserByEmail(String email) throws VoixDeSagesseException {
        return userRepository.findByEmail(email).orElseThrow(() -> new VoixDeSagesseException("USER_NOT_FOUND")).toDTO();
    }

    @Override
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // The extension comes from the validated image type, never from the client file name, so an
        // uploaded file can only ever be served back as an image.
        String fileExtension = IMAGE_EXTENSIONS.get(String.valueOf(file.getContentType()).toLowerCase());
        if (fileExtension == null)
            throw new IOException("Unsupported image type");
        String uniqueFileName = "profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        String profilePictureUrl = "/uploads/profile-pictures/" + uniqueFileName;

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                deleteOldProfilePicture(user.getProfilePicture());
            }

            user.setProfilePicture(profilePictureUrl);
            userRepository.save(user);
        }

        return profilePictureUrl;
    }

    private void deleteOldProfilePicture(String oldPictureUrl) {
        try {
            String fileName = oldPictureUrl.substring(oldPictureUrl.lastIndexOf("/") + 1);
            Path oldFilePath = Paths.get(uploadDir).resolve(fileName);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        } catch (IOException e) {
            log.error("Erreur lors de la suppression de l'ancienne image: " + e.getMessage());
        }
    }

    @Override
    public void saveArticle(Long userId, Long articleId) throws VoixDeSagesseException {
        if (!userRepository.existsById(userId))
            throw new VoixDeSagesseException("USER_NOT_FOUND");
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));
        if (isArticleSaved(userId, articleId))
            throw new VoixDeSagesseException("ALREADY_SAVED");
        userRepository.addSavedArticle(userId, articleId);
        notificationService.articleSaved(article, userId);
    }

    @Override
    public void unsaveArticle(Long userId, Long articleId) throws VoixDeSagesseException {
        if (!isArticleSaved(userId, articleId)) {
            throw new VoixDeSagesseException("NOT_SAVED");
        }
        userRepository.removeSavedArticle(userId, articleId);
        articleRepository.findById(articleId).ifPresent(
                article -> notificationService.withdraw(NotificationType.SAVE, article.getUserId(), userId, articleId));
    }

    @Override
    public boolean isArticleSaved(Long userId, Long articleId) {
        return userRepository.isArticleSaved(userId, articleId);
    }

    @Override
    public void removeArticleFromAllUsersLikes(Long articleId) {
        userRepository.removeArticleFromAllUsersLikes(articleId);
    }

    @Override
    public void removeArticleFromAllUsersSaved(Long articleId) {
        userRepository.removeArticleFromAllUsersSaved(articleId);
    }

    @Override
    public void decrementLikesReceivedByAmount(Long userId, Long amount) {
        if (amount > 0) {
            userRepository.decrementLikesReceivedByAmount(userId, -amount);
        }
    }

    @Override
    public void addReportedArticle(Long userId, Long articleId) throws VoixDeSagesseException {
        User user = getUserById(userId);
        if (user.getReportedArticlesId() == null) {
            user.setReportedArticlesId(new HashSet<>());
        }
        user.getReportedArticlesId().add(articleId);
        userRepository.save(user);
    }

    @Override
    public void removeReportedArticle(Long userId, Long articleId) throws VoixDeSagesseException {
        User user = getUserById(userId);
        if (user.getReportedArticlesId() != null) {
            user.getReportedArticlesId().remove(articleId);
            userRepository.save(user);
        }
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long getNewUsersToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return userRepository.countByDateInscriptionBetween(startOfDay, endOfDay);
    }

    @Override
    public List<UserDTO> getAllUsers() throws VoixDeSagesseException {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserAccount(Long userId) throws VoixDeSagesseException {
        log.info("Début de la suppression du compte utilisateur: {}", userId);

        User user = getUserById(userId);

        // 1. Supprimer la photo de profil du système de fichiers
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                deleteOldProfilePicture(user.getProfilePicture());
                log.info("Photo de profil supprimée pour l'utilisateur: {}", userId);
            } catch (Exception e) {
                log.error("Erreur lors de la suppression de la photo de profil: {}", e.getMessage());
            }
        }

        // 2. Supprimer tous les commentaires de l'utilisateur
        commonService.deleteAllCommentsByUser(userId);
        log.info("Commentaires supprimés pour l'utilisateur: {}", userId);

        // 3. Récupérer tous les articles de l'utilisateur
        List<Article> userArticles = articleRepository.findByUserId(userId);

        // 4. Pour chaque article de l'utilisateur
        for (Article article : userArticles) {
            // Supprimer l'article de toutes les listes de likes des autres utilisateurs
            userRepository.removeArticleFromAllUsersLikes(article.getId());

            // Supprimer l'article de toutes les listes sauvegardées des autres utilisateurs
            userRepository.removeArticleFromAllUsersSaved(article.getId());

            // Supprimer tous les commentaires sur cet article
            commonService.deleteAllCommentsByArticle(article.getId());

            // Supprimer tous les signalements liés à cet article
            signalRepository.deleteByArticleId(article.getId());
        }

        // 5. Supprimer tous les articles de l'utilisateur
        articleRepository.deleteByUserId(userId);
        log.info("Articles supprimés pour l'utilisateur: {}", userId);

        // 6. Gérer les relations de suivi
        // Retirer l'utilisateur de la liste following de tous ceux qui le suivent
        List<User> followers = userRepository.findByFollowingIdContaining(userId);
        for (User follower : followers) {
            follower.getFollowingId().remove(userId);
            follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
            userRepository.save(follower);
        }

        // Pour chaque utilisateur que l'utilisateur supprimé suivait, décrémenter leur
        // compteur de followers
        if (user.getFollowingId() != null && !user.getFollowingId().isEmpty()) {
            for (Long followedUserId : user.getFollowingId()) {
                try {
                    User followedUser = getUserById(followedUserId);
                    followedUser.setFollowersCount(Math.max(0, followedUser.getFollowersCount() - 1));
                    userRepository.save(followedUser);
                } catch (VoixDeSagesseException e) {
                    log.error("Erreur lors de la mise à jour du follower count pour l'utilisateur: {}", followedUserId);
                }
            }
        }

        // 7. Gérer les likes donnés par l'utilisateur
        if (user.getLikedArticlesId() != null && !user.getLikedArticlesId().isEmpty()) {
            for (Long articleId : user.getLikedArticlesId()) {
                try {
                    Article article = articleRepository.findById(articleId).orElse(null);
                    if (article != null) {
                        // Décrémenter le compteur de likes de l'article
                        article.setLikes(Math.max(0, article.getLikes() - 1));
                        articleRepository.save(article);

                        // Décrémenter les likes reçus par l'auteur de l'article
                        try {
                            User articleAuthor = getUserById(article.getUserId());
                            articleAuthor.setLikesReceived(Math.max(0, articleAuthor.getLikesReceived() - 1));
                            userRepository.save(articleAuthor);
                        } catch (VoixDeSagesseException e) {
                            log.error("Erreur lors de la mise à jour des likes reçus pour l'auteur: {}",
                                    article.getUserId());
                        }
                    }
                } catch (Exception e) {
                    log.error("Erreur lors de la suppression du like pour l'article: {}", articleId);
                }
            }
        }

        // 8. Supprimer tous les signalements faits par l'utilisateur
        signalRepository.deleteByReporterId(userId);

        // 9. Supprimer tous les signalements contre l'utilisateur
        signalRepository.deleteByReportedUserId(userId);

        // Notifications received or caused by the user
        notificationService.userDeleted(userId);

        // 10. Supprimer l'OTP si existe
        otpRepository.deleteById(user.getEmail());

        // 11. Finalement, supprimer l'utilisateur
        userRepository.deleteById(userId);
        log.info("Compte utilisateur supprimé avec succès: {}", userId);
    }

    
}
