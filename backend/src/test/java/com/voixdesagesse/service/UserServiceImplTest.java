package com.voixdesagesse.service;
import com.voixdesagesse.dto.*;
import com.voixdesagesse.entity.OTP;
import com.voixdesagesse.entity.PendingRegistration;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.mail.MailService;
import com.voixdesagesse.repository.OTPRepository;
import com.voixdesagesse.repository.PendingRegistrationRepository;
import com.voixdesagesse.repository.UserRepository;
import com.voixdesagesse.utility.Utilities;
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
    private PendingRegistrationRepository pendingRegistrationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private NotificationService notificationService;

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


    }

    @Test
    @DisplayName("Inscription - envoie un code sans créer le compte")
    void testStartRegistrationSendsCodeOnly() throws VoixDeSagesseException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.startRegistration(registrationDTO);

        ArgumentCaptor<PendingRegistration> pending = ArgumentCaptor.forClass(PendingRegistration.class);
        verify(pendingRegistrationRepository).save(pending.capture());
        assertEquals("encodedPassword", pending.getValue().getMotdepasse());
        assertTrue(pending.getValue().getOtpCode().matches("[0-9]{6}"));
        verify(mailService).sendHtml(eq("test@example.com"), anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Inscription - email déjà utilisé : aucun code envoyé")
    void testStartRegistrationUserAlreadyExists() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.startRegistration(registrationDTO));
        assertEquals("USER_FOUND", exception.getMessage());
        verify(pendingRegistrationRepository, never()).save(any(PendingRegistration.class));
        verify(mailService, never()).sendHtml(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Inscription - refusée si un code a été envoyé il y a moins d'une minute")
    void testStartRegistrationTooSoon() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(pendingRegistrationRepository.findById(anyString())).thenReturn(Optional.of(pending(LocalDateTime.now().minusSeconds(20), 0)));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.startRegistration(registrationDTO));
        assertEquals("OTP_TOO_SOON", exception.getMessage());
        verify(mailService, never()).sendHtml(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Inscription - si l'email ne part pas, l'inscription en attente est supprimée")
    void testStartRegistrationMailFailure() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doThrow(new VoixDeSagesseException("MAIL_FAILED")).when(mailService).sendHtml(anyString(), anyString(), anyString());

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.startRegistration(registrationDTO));
        assertEquals("MAIL_FAILED", exception.getMessage());
        verify(pendingRegistrationRepository).delete(any(PendingRegistration.class));
    }

    @Test
    @DisplayName("Confirmation - le bon code crée le compte, toujours avec le rôle USER")
    void testConfirmRegistrationCreatesUser() throws VoixDeSagesseException {
        PendingRegistration pending = pending(LocalDateTime.now(), 0);
        when(pendingRegistrationRepository.findById(anyString())).thenReturn(Optional.of(pending));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Utilities> utilities = mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getNextSequence("users")).thenReturn(1L);
            userService.confirmRegistration("test@example.com", "123456");
        }

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(AccountType.USER, saved.getValue().getAccountType());
        assertEquals("encodedPassword", saved.getValue().getMotdepasse());
        verify(pendingRegistrationRepository).delete(pending);
    }

    @Test
    @DisplayName("Confirmation - un mauvais code ne crée pas le compte")
    void testConfirmRegistrationWrongCode() {
        PendingRegistration pending = pending(LocalDateTime.now(), 0);
        when(pendingRegistrationRepository.findById(anyString())).thenReturn(Optional.of(pending));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.confirmRegistration("test@example.com", "654321"));
        assertEquals("OTP_INCORRECT", exception.getMessage());
        assertEquals(1, pending.getAttempts());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Confirmation - l'inscription est annulée après 5 essais incorrects")
    void testConfirmRegistrationTooManyAttempts() {
        PendingRegistration pending = pending(LocalDateTime.now(), 4);
        when(pendingRegistrationRepository.findById(anyString())).thenReturn(Optional.of(pending));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.confirmRegistration("test@example.com", "654321"));
        assertEquals("OTP_TOO_MANY_ATTEMPTS", exception.getMessage());
        verify(pendingRegistrationRepository).delete(pending);
    }

    @Test
    @DisplayName("Confirmation - code expiré")
    void testConfirmRegistrationExpired() {
        when(pendingRegistrationRepository.findById(anyString())).thenReturn(Optional.of(pending(LocalDateTime.now().minusMinutes(11), 0)));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.confirmRegistration("test@example.com", "123456"));
        assertEquals("OTP_NOT_FOUND", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    private PendingRegistration pending(LocalDateTime creationTime, int attempts) {
        return new PendingRegistration("test@example.com", "Test", "User", "encodedPassword", "123456", creationTime, attempts);
    }

    @Test
    @DisplayName("Test envoi OTP - succès")
    void testSendOtpSuccess() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(otpRepository.save(any(OTP.class))).thenReturn(new OTP());

        try (MockedStatic<Utilities> utilities = mockStatic(Utilities.class)) {
            utilities.when(Utilities::generateOTP).thenReturn("123456");

            Boolean result = userService.sendOtp("test@example.com");

            assertTrue(result);
            verify(otpRepository).save(any(OTP.class));
            verify(mailService).sendHtml(eq("test@example.com"), anyString(), anyString());
        }
    }

    @Test
    @DisplayName("Envoi OTP - email inconnu : même réponse, aucun email envoyé")
    void testSendOtpUserNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertTrue(userService.sendOtp("unknown@example.com"));
        verify(otpRepository, never()).save(any(OTP.class));
        verify(mailService, never()).sendHtml(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Envoi OTP - refusé si un code a été envoyé il y a moins d'une minute")
    void testSendOtpTooSoon() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(otpRepository.findById(anyString()))
            .thenReturn(Optional.of(new OTP("test@example.com", "123456", LocalDateTime.now().minusSeconds(20))));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class, () -> userService.sendOtp("test@example.com"));
        assertEquals("OTP_TOO_SOON", exception.getMessage());
        verify(mailService, never()).sendHtml(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test vérification OTP - succès")
    void testVerifyOtpSuccess() throws VoixDeSagesseException {
        OTP otp = new OTP("test@example.com", "123456", LocalDateTime.now());
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        String resetToken = userService.verifyOtp("test@example.com", "123456");

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
        when(otpRepository.findById(anyString())).thenReturn(Optional.empty());

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_NOT_FOUND", exception.getMessage());
    }

    @Test
    @DisplayName("Test vérification OTP - OTP incorrect")
    void testVerifyOtpIncorrect() {
        OTP otp = new OTP("test@example.com", "654321", LocalDateTime.now());
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_INCORRECT", exception.getMessage());
        assertEquals(1, otp.getAttempts());
        verify(otpRepository).save(otp);
    }

    @Test
    @DisplayName("Test changement de mot de passe - succès")
    void testChangePasswordSuccess() throws VoixDeSagesseException {
        OTP otp = new OTP("test@example.com", null, LocalDateTime.now(), 0, "reset-token");
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseDTO result = userService.changePassword(
            new ResetPasswordRequest("test@example.com", "reset-token", "Password123!"));

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
        LocalDateTime oldTime = LocalDateTime.now().minusMinutes(10);
        OTP expiredOTP1 = new OTP("test1@example.com", "111111", oldTime);
        OTP expiredOTP2 = new OTP("test2@example.com", "222222", oldTime);
        List<OTP> expiredOTPs = Arrays.asList(expiredOTP1, expiredOTP2);

        when(otpRepository.findByCreationTimeBefore(any(LocalDateTime.class))).thenReturn(expiredOTPs);

        userService.removeExpiredOTPs();

        verify(otpRepository).deleteAll(expiredOTPs);
    }

    @Test
    @DisplayName("Les inscriptions jamais confirmées sont supprimées après 10 minutes")
    void testRemoveExpiredPendingRegistrations() {
        List<PendingRegistration> expired = List.of(pending(LocalDateTime.now().minusMinutes(11), 0));
        when(pendingRegistrationRepository.findByCreationTimeBefore(any(LocalDateTime.class))).thenReturn(expired);

        userService.removeExpiredOTPs();

        verify(pendingRegistrationRepository).deleteAll(expired);
    }

    @Test
    @DisplayName("Test mise à jour profil utilisateur - succès")
    void testUpdateUserProfileSuccess() throws VoixDeSagesseException {
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(1L);
        profileDTO.setNom("UpdatedNom");
        profileDTO.setPrenom("UpdatedPrenom");
        profileDTO.setProfilePicture("newpic.jpg");
        profileDTO.setBio("New bio");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserProfileDTO result = userService.updateUserProfile(1L, profileDTO);

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
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(999L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        VoixDeSagesseException exception = assertThrows(VoixDeSagesseException.class,
            () -> userService.updateUserProfile(999L, profileDTO));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }
    
}
