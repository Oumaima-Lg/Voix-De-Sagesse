package com.voixdesagesse.VoixDeSagesse.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Data;
import com.voixdesagesse.VoixDeSagesse.entity.OTP;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.OTPRepository;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "userService")
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public UserRegistrationDTO registerUser(UserRegistrationDTO userDTO) throws ArticlaException {

        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if (optional.isPresent())
            throw new ArticlaException("USER_FOUND");

        userDTO.setId(Utilities.getNextSequence("users"));
        userDTO.setMotdepasse(passwordEncoder.encode(userDTO.getMotdepasse()));

        User user = userDTO.toEntity();
        user.setUsername(user.getPrenom());
        user.setFollowersCount(0L);
        user.setContentCount(0L);
        user.setFollowingCount(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user.setSavedArticlesId(new HashSet<>());
        user = userRepository.save(user);

        return user.toRegisterDTO();
    }

    @Override
    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException {

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(loginDTO.getMotdepasse(), user.getMotdepasse()))
            throw new ArticlaException("INVALID_CREDENTIALS");

        return user.toDTO();
    }

    @Override
    public Boolean sendOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        MimeMessage mm = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mm, true);
        message.setTo(email);
        message.setSubject("Your OTP Code");
        String genOtp = Utilities.generateOTP();
        OTP otp = new OTP(email, genOtp, LocalDateTime.now());
        otpRepository.save(otp);
        // message.setText("Your Code is : " + genOtp, false);
        message.setText(Data.getMessageBody(genOtp, user.getNom(), user.getPrenom()), true);
        message.setFrom("laghjibioumaima2003@gmail.com");
        mailSender.send(mm);
        return true;
    }

    @Override
    public Boolean verifyOtp(String email, String otp) throws ArticlaException {
        OTP otpEntity = otpRepository.findById(email).orElseThrow(() -> new ArticlaException("OTP_NOT_FOUND"));

        if (!otpEntity.getOtpCode().equals((otp)))
            throw new ArticlaException("OTP_INCORRECT");
        return true;

    }

    @Override
    public ResponseDTO changePassword(LoginDTO loginDTO) throws ArticlaException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        user.setMotdepasse(passwordEncoder.encode(loginDTO.getMotdepasse()));
        userRepository.save(user);
        return new ResponseDTO("Password changed successfully.");
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTPs() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<OTP> expiredOTPs = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOTPs.isEmpty()) {
            otpRepository.deleteAll(expiredOTPs);
        }
    }

    @Override
    public UserProfileDTO updateUserProfile(UserProfileDTO profileDTO) throws ArticlaException {
        User user = userRepository.findById(profileDTO.getId())
                .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        user.setNom(profileDTO.getNom());
        user.setPrenom(profileDTO.getPrenom());
        user.setProfilePicture(profileDTO.getProfilePicture());
        user.setBio(profileDTO.getBio());
        userRepository.save(user);
        return profileDTO;
    }

    @Override
    public User getUserById(long userId) throws ArticlaException {
        return userRepository.findById(userId).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
    }

    @Transactional
    @Override
    public synchronized void addLikedArticle(Long userId, Long articleId) throws ArticlaException {
        User user = getUserById(userId);
        Set<Long> likedArticles = user.getLikedArticlesId();

        if (likedArticles == null)
            likedArticles = new HashSet<>();

        if (likedArticles.contains(articleId))
            throw new ArticlaException("Article déjà liké par cet utilisateur");

        likedArticles.add(articleId);
        user.setLikedArticlesId(likedArticles);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public synchronized void removeLikedArticle(Long userId, Long articleId) throws ArticlaException {
        User user = getUserById(userId);
        Set<Long> likedArticles = user.getLikedArticlesId();

        if (likedArticles == null || !likedArticles.contains(articleId)) {
            throw new ArticlaException("Article pas encore liké par cet utilisateur");
        }

        likedArticles.remove(articleId);
        user.setLikedArticlesId(likedArticles);
        userRepository.save(user);
    }

    
    @Transactional
    @Override
    public synchronized void incrementLikesReceived(Long userId) throws ArticlaException {
        User user = getUserById(userId);

        Long currentLikes = user.getLikesReceived();
        if (currentLikes == null) {
            currentLikes = 0L;
        }

        user.setLikesReceived(currentLikes + 1);
        userRepository.save(user);
        log.debug("INCREMENT: User " + userId + " now has " + (currentLikes + 1) + " likes received");
    }


    @Transactional
    @Override
    public synchronized void decrementLikesReceived(Long userId) throws ArticlaException {
        User user = getUserById(userId);

        Long currentLikes = user.getLikesReceived();
        if (currentLikes == null || currentLikes <= 0) {
            user.setLikesReceived(0L);
            userRepository.save(user);
            log.debug("DECREMENT: User " + userId + " likes already at 0, no change");
            return;
        }

        user.setLikesReceived(currentLikes - 1);
        userRepository.save(user);
        log.debug("DECREMENT: User " + userId + " now has " + (currentLikes - 1) + " likes received");
    }

    @Override
    @Transactional
    public void incrementContentCount(Long userId) {
        userRepository.incrementContentCount(userId);
    }

    @Override
    @Transactional
    public void decrementContentCount(Long userId) {
        userRepository.decrementContentCount(userId);
    }


    @Override
    @Transactional
    public void followUser(Long currentUserId, Long targetUserId) throws ArticlaException {

        if (currentUserId.equals(targetUserId)) 
            throw new ArticlaException("Vous ne pouvez pas vous suivre vous-même");
        
        if (!userRepository.existsById(currentUserId)) 
            throw new ArticlaException("Utilisateur actuel introuvable");
        
        if (!userRepository.existsById(targetUserId))
            throw new ArticlaException("Utilisateur cible introuvable");

        if (isFollowing(currentUserId, targetUserId))
            throw new ArticlaException("Vous suivez déjà cet utilisateur");

        addFollowing(currentUserId, targetUserId);
        incrementFollowingCount(currentUserId);
        incrementFollowersCount(targetUserId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long currentUserId, Long targetUserId) throws ArticlaException {
        if (currentUserId.equals(targetUserId))
            throw new ArticlaException("Vous ne pouvez pas vous désabonner de vous-même");

        if (!isFollowing(currentUserId, targetUserId))
            throw new ArticlaException("Vous ne suivez pas cet utilisateur");

        removeFollowing(currentUserId, targetUserId);
        decrementFollowingCount(currentUserId);
        decrementFollowersCount(targetUserId);
    }

    @Override
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        return userRepository.isFollowing(currentUserId, targetUserId);
    }

    @Override
    @Transactional
    public void addFollowing(Long currentUserId, Long targetUserId) {
        userRepository.addFollowing(currentUserId, targetUserId);
    }

    @Override
    @Transactional
    public void removeFollowing(Long currentUserId, Long targetUserId) {
        userRepository.removeFollowing(currentUserId, targetUserId);
    }

    @Override
    @Transactional
    public void incrementFollowingCount(Long userId) {
        userRepository.incrementFollowingCount(userId);
    }

    @Override
    @Transactional
    public void decrementFollowingCount(Long userId) {
        userRepository.decrementFollowingCount(userId);
    }

    @Override
    @Transactional
    public void incrementFollowersCount(Long userId) {
        userRepository.incrementFollowersCount(userId);
    }

    @Override
    @Transactional
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
    public UserDTO getUserByEmail(String email) throws ArticlaException {
        return userRepository.findByEmail(email).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND")).toDTO();
    }

    @Override
    public UserProfileDTO getUserProfileById(long userId) throws ArticlaException {
        User user = getUserById(userId);
        return user.toProfileDTO();
    }

    @Override
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
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
    @Transactional
    public void saveArticle(Long userId, Long articleId) throws ArticlaException {
        if (!userRepository.existsById(userId))
            throw new ArticlaException("USER_NOT_FOUND");
        if (isArticleSaved(userId, articleId))
            throw new ArticlaException("Article déjà sauvegardé");
        userRepository.addSavedArticle(userId, articleId);
    }

    @Override
    @Transactional
    public void unsaveArticle(Long userId, Long articleId) throws ArticlaException {
        if (!isArticleSaved(userId, articleId)) {
            throw new ArticlaException("Article non sauvegardé");
        }
        userRepository.removeSavedArticle(userId, articleId);
    }

    @Override
    public boolean isArticleSaved(Long userId, Long articleId) {
        return userRepository.isArticleSaved(userId, articleId);
    }


    @Override
    @Transactional
    public void removeArticleFromAllUsersLikes(Long articleId) {
        userRepository.removeArticleFromAllUsersLikes(articleId);
    }

    @Override
    @Transactional
    public void removeArticleFromAllUsersSaved(Long articleId) {
        userRepository.removeArticleFromAllUsersSaved(articleId);
    }

    @Override
    @Transactional
    public void decrementLikesReceivedByAmount(Long userId, Long amount) {
        if (amount > 0) {
            userRepository.decrementLikesReceivedByAmount(userId, -amount);
        }
    }

}
