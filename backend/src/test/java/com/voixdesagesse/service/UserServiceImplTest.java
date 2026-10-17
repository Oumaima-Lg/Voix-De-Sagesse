package com.voixdesagesse.service;
import com.voixdesagesse.dto.*;
import com.voixdesagesse.entity.OTP;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.OTPRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.utility.Utilities;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail("test@example.com");
        user.setMotdepasse("encodedPassword");
        user.setAccountType(AccountType.USER);

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setNom("Test");
        registrationDTO.setPrenom("User");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setMotdepasse("Password123!");
        registrationDTO.setAccountType(AccountType.USER);

        ReflectionTestUtils.setField(userService, "mailFrom", "no-reply@test.local");

    }

    @Test
    @DisplayName("Test enregistrement utilisateur - succès")
    void testRegisterUserSuccess() throws VoixDeSagesseException {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<Utilities> utilities = mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getNextSequence("users")).thenReturn(1L);

            // When
            UserRegistrationDTO result = userService.registerUser(registrationDTO);

            // Then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getNom(), result.getNom());
            verify(userRepository).save(any(User.class));
            verify(passwordEncoder).encode("Password123!");
        }
    }

    @Test
    @DisplayName("Inscription - le rôle est toujours USER, même si ADMIN est demandé")
    void testRegisterUserIgnoresRequestedRole() throws VoixDeSagesseException {
        registrationDTO.setAccountType(AccountType.ADMIN);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Utilities> utilities = mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getNextSequence("users")).thenReturn(1L);
            userService.registerUser(registrationDTO);
        }

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(AccountType.USER, saved.getValue().getAccountType());
    }

    @Test
    @DisplayName("Test enregistrement utilisateur - utilisateur déjà existant")
    void testRegisterUserAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.registerUser(registrationDTO));
        assertEquals("USER_FOUND", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test envoi OTP - succès")
    void testSendOtpSuccess() throws Exception {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(otpRepository.save(any(OTP.class))).thenReturn(new OTP());

        try (MockedStatic<Utilities> utilities = mockStatic(Utilities.class)) {
            utilities.when(Utilities::generateOTP).thenReturn("123456");

            // When
            Boolean result = userService.sendOtp("test@example.com");

            // Then
            assertTrue(result);
            verify(otpRepository).save(any(OTP.class));
            verify(mailSender).send(any(MimeMessage.class));
        }
    }

    @Test
    @DisplayName("Envoi OTP - email inconnu : même réponse, aucun email envoyé")
    void testSendOtpUserNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertTrue(userService.sendOtp("unknown@example.com"));
        verify(otpRepository, never()).save(any(OTP.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Envoi OTP - refusé si un code a été envoyé il y a moins d'une minute")
    void testSendOtpTooSoon() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(otpRepository.findById(anyString()))
            .thenReturn(Optional.of(new OTP("test@example.com", "123456", LocalDateTime.now().minusSeconds(20))));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> userService.sendOtp("test@example.com"));
        assertEquals("OTP_TOO_SOON", exception.getMessage());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Test vérification OTP - succès")
    void testVerifyOtpSuccess() throws VoixDeSagesseException {
        // Given
        OTP otp = new OTP("test@example.com", "123456", LocalDateTime.now());
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        // When
        String resetToken = userService.verifyOtp("test@example.com", "123456");

        // Then: a reset token is issued and the code cannot be reused
        assertNotNull(resetToken);
        assertEquals(resetToken, otp.getResetToken());
        assertNull(otp.getOtpCode());
        verify(otpRepository).save(otp);
    }

    @Test
    @DisplayName("Vérification OTP - code expiré")
    void testVerifyOtpExpired() {
        OTP otp = new OTP("test@example.com", "123456", LocalDateTime.now().minusMinutes(11));
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_NOT_FOUND", exception.getMessage());
    }

    @Test
    @DisplayName("Vérification OTP - le code est supprimé après 5 essais incorrects")
    void testVerifyOtpTooManyAttempts() {
        OTP otp = new OTP("test@example.com", "654321", LocalDateTime.now(), 4, null);
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_TOO_MANY_ATTEMPTS", exception.getMessage());
        verify(otpRepository).delete(otp);
    }

    @Test
    @DisplayName("Test vérification OTP - OTP non trouvé")
    void testVerifyOtpNotFound() {
        // Given
        when(otpRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_NOT_FOUND", exception.getMessage());
    }

    @Test
    @DisplayName("Test vérification OTP - OTP incorrect")
    void testVerifyOtpIncorrect() {
        // Given
        OTP otp = new OTP("test@example.com", "654321", LocalDateTime.now());
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        // When & Then
        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_INCORRECT", exception.getMessage());
        assertEquals(1, otp.getAttempts());
        verify(otpRepository).save(otp);
    }

    @Test
    @DisplayName("Test changement de mot de passe - succès")
    void testChangePasswordSuccess() throws VoixDeSagesseException {
        // Given: the OTP was verified and a reset token issued
        OTP otp = new OTP("test@example.com", null, LocalDateTime.now(), 0, "reset-token");
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        ResponseDTO result = userService.changePassword(
            new ResetPasswordRequest("test@example.com", "reset-token", "Password123!"));

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123!");
        verify(otpRepository).delete(otp);
    }

    @Test
    @DisplayName("Changement de mot de passe - refusé sans code vérifié")
    void testChangePasswordWithoutVerifiedOtp() {
        when(otpRepository.findById(anyString())).thenReturn(Optional.empty());

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> userService.changePassword(
            new ResetPasswordRequest("test@example.com", "anything", "Password123!")));
        assertEquals("RESET_TOKEN_INVALID", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Changement de mot de passe - refusé avec un mauvais jeton")
    void testChangePasswordWrongToken() {
        OTP otp = new OTP("test@example.com", null, LocalDateTime.now(), 0, "reset-token");
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> userService.changePassword(
            new ResetPasswordRequest("test@example.com", "forged-token", "Password123!")));
        assertEquals("RESET_TOKEN_INVALID", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test suppression OTP expirés")
    void testRemoveExpiredOTPs() {
        // Given
        LocalDateTime oldTime = LocalDateTime.now().minusMinutes(10);
        OTP expiredOTP1 = new OTP("test1@example.com", "111111", oldTime);
        OTP expiredOTP2 = new OTP("test2@example.com", "222222", oldTime);
        List<OTP> expiredOTPs = Arrays.asList(expiredOTP1, expiredOTP2);

        when(otpRepository.findByCreationTimeBefore(any(LocalDateTime.class))).thenReturn(expiredOTPs);

        // When
        userService.removeExpiredOTPs();

        // Then
        verify(otpRepository).deleteAll(expiredOTPs);
    }

    @Test
    @DisplayName("Test mise à jour profil utilisateur - succès")
    void testUpdateUserProfileSuccess() throws VoixDeSagesseException {
        // Given
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(1L);
        profileDTO.setNom("UpdatedNom");
        profileDTO.setPrenom("UpdatedPrenom");
        profileDTO.setProfilePicture("newpic.jpg");
        profileDTO.setBio("New bio");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserProfileDTO result = userService.updateUserProfile(1L, profileDTO);

        // Then
        assertNotNull(result);
        assertEquals(profileDTO.getNom(), result.getNom());
        assertEquals(profileDTO.getPrenom(), result.getPrenom());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Mise à jour profil - la photo ne peut pas être modifiée par ce biais")
    void testUpdateUserProfileIgnoresPicture() throws VoixDeSagesseException {
        user.setProfilePicture("/uploads/profile-pictures/profile_1_1.png");
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setNom("Nom");
        profileDTO.setPrenom("Prenom");
        profileDTO.setProfilePicture("/uploads/profile-pictures/profile_2_2.png");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserProfile(1L, profileDTO);

        assertEquals("/uploads/profile-pictures/profile_1_1.png", user.getProfilePicture());
    }

    @Test
    @DisplayName("Upload photo - l'extension vient du type d'image, pas du nom de fichier")
    void testSaveProfilePictureUsesImageExtension(@TempDir Path uploadDir) throws IOException {
        ReflectionTestUtils.setField(userService, "uploadDir", uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("profilePicture", "evil.html", "image/png", new byte[] { 1, 2, 3 });

        String url = userService.saveProfilePicture(file, 1L);

        assertTrue(url.endsWith(".png"), url);
    }

    @Test
    @DisplayName("Upload photo - un type qui n'est pas une image est refusé")
    void testSaveProfilePictureRejectsNonImage(@TempDir Path uploadDir) {
        ReflectionTestUtils.setField(userService, "uploadDir", uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("profilePicture", "evil.html", "text/html", new byte[] { 1 });

        assertThrows(IOException.class, () -> userService.saveProfilePicture(file, 1L));
    }

    @Test
    @DisplayName("Test mise à jour profil - utilisateur non trouvé")
    void testUpdateUserProfileNotFound() {
        // Given
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(999L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.updateUserProfile(999L, profileDTO));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }
    
}
