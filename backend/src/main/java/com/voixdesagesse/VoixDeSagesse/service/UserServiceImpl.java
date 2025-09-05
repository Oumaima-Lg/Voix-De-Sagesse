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

import org.springframework.beans.factory.annotation.Autowired;
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

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Value("${file.upload-dir:uploads/profile-pictures}")
    private String uploadDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

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
        // save into a repo Image
        user.setProfilePicture(profileDTO.getProfilePicture());
        user.setBio(profileDTO.getBio());
        userRepository.save(user);
        return profileDTO;
    }

    @Override
    public User getUserById(long userId) throws ArticlaException {
        return userRepository.findById(userId).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
    }

    @Override
    @Transactional
    public void addLikedArticle(Long currentUserId, Long articleId) {
        userRepository.addLikedArticle(currentUserId, articleId);
    }

    @Override
    @Transactional
    public void removeLikedArticle(Long currentUserId, Long articleId) {
        userRepository.removeLikedArticle(currentUserId, articleId);
    }

    @Override
    @Transactional
    public void incrementLikesReceived(Long userId) {
        userRepository.incrementLikesReceived(userId);
    }

    @Override
    @Transactional
    public void decrementLikesReceived(Long userId) {
        userRepository.decrementLikesReceived(userId);
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

    // ✅ Nouvelles méthodes pour le système de suivi

    @Override
    @Transactional
    public void followUser(Long currentUserId, Long targetUserId) {
        // Vérifier que l'utilisateur ne se suit pas lui-même
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Vous ne pouvez pas vous suivre vous-même");
        }

        // Vérifier que les deux utilisateurs existent
        if (!userRepository.existsById(currentUserId)) {
            throw new RuntimeException("Utilisateur actuel introuvable");
        }
        if (!userRepository.existsById(targetUserId)) {
            throw new RuntimeException("Utilisateur cible introuvable");
        }

        // Vérifier si déjà suivi
        if (isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Vous suivez déjà cet utilisateur");
        }

        // Effectuer le suivi
        addFollowing(currentUserId, targetUserId);
        incrementFollowingCount(currentUserId);
        incrementFollowersCount(targetUserId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long currentUserId, Long targetUserId) {
        // Vérifier que l'utilisateur ne se désuit pas lui-même
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Vous ne pouvez pas vous désabonner de vous-même");
        }

        // Vérifier si actuellement suivi
        if (!isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Vous ne suivez pas cet utilisateur");
        }

        // Effectuer le désuivi
        removeFollowing(currentUserId, targetUserId);
        decrementFollowingCount(currentUserId);
        decrementFollowersCount(targetUserId);
    }

    @Override
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        return userRepository.isFollowing(currentUserId, targetUserId);
    }

    // Méthodes internes
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
        // Créer le dossier s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique pour le fichier
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String uniqueFileName = "profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;

        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Construire l'URL accessible
        String profilePictureUrl = "/uploads/profile-pictures/" + uniqueFileName;

        // Mettre à jour l'utilisateur en base
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Supprimer l'ancienne image si elle existe
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
            // Extraire le nom de fichier de l'URL
            String fileName = oldPictureUrl.substring(oldPictureUrl.lastIndexOf("/") + 1);
            Path oldFilePath = Paths.get(uploadDir).resolve(fileName);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        } catch (IOException e) {
            // Log l'erreur mais ne pas faire échouer l'upload
            System.err.println("Erreur lors de la suppression de l'ancienne image: " + e.getMessage());
        }
    }

}
