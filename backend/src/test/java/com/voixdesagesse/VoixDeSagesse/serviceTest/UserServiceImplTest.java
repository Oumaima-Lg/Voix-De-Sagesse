package com.voixdesagesse.VoixDeSagesse.serviceTest;
import com.voixdesagesse.VoixDeSagesse.dto.*;
import com.voixdesagesse.VoixDeSagesse.entity.OTP;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.OTPRepository;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import com.voixdesagesse.VoixDeSagesse.service.UserServiceImpl;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private LoginDTO loginDTO;

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

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setMotdepasse("Password123!");
    }

    @Test
    @DisplayName("Test enregistrement utilisateur - succès")
    void testRegisterUserSuccess() throws ArticlaException {
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
    @DisplayName("Test enregistrement utilisateur - utilisateur déjà existant")
    void testRegisterUserAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        ArticlaException exception = assertThrows(ArticlaException.class,
            () -> userService.registerUser(registrationDTO));
        assertEquals("USER_FOUND", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test connexion utilisateur - succès")
    void testLoginUserSuccess() throws ArticlaException {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        UserDTO result = userService.loginUser(loginDTO);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        verify(passwordEncoder).matches("Password123!", "encodedPassword");
    }

    @Test
    @DisplayName("Test connexion utilisateur - utilisateur non trouvé")
    void testLoginUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        ArticlaException exception = assertThrows(ArticlaException.class,
            () -> userService.loginUser(loginDTO));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }

    @Test
    @DisplayName("Test connexion utilisateur - mot de passe incorrect")
    void testLoginUserInvalidPassword() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        ArticlaException exception = assertThrows(ArticlaException.class,
            () -> userService.loginUser(loginDTO));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());
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
    @DisplayName("Test envoi OTP - utilisateur non trouvé")
    void testSendOtpUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ArticlaException.class, () -> userService.sendOtp("test@example.com"));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Test vérification OTP - succès")
    void testVerifyOtpSuccess() throws ArticlaException {
        // Given
        OTP otp = new OTP("test@example.com", "123456", LocalDateTime.now());
        when(otpRepository.findById(anyString())).thenReturn(Optional.of(otp));

        // When
        Boolean result = userService.verifyOtp("test@example.com", "123456");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Test vérification OTP - OTP non trouvé")
    void testVerifyOtpNotFound() {
        // Given
        when(otpRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        ArticlaException exception = assertThrows(ArticlaException.class,
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
        ArticlaException exception = assertThrows(ArticlaException.class,
            () -> userService.verifyOtp("test@example.com", "123456"));
        assertEquals("OTP_INCORRECT", exception.getMessage());
    }

    @Test
    @DisplayName("Test changement de mot de passe - succès")
    void testChangePasswordSuccess() throws ArticlaException {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        ResponseDTO result = userService.changePassword(loginDTO);

        // Then
        assertNotNull(result);
        assertEquals("Password changed successfully.", result.getMessage());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123!");
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
    void testUpdateUserProfileSuccess() throws ArticlaException {
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
        UserProfileDTO result = userService.updateUserProfile(profileDTO);

        // Then
        assertNotNull(result);
        assertEquals(profileDTO.getNom(), result.getNom());
        assertEquals(profileDTO.getPrenom(), result.getPrenom());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test mise à jour profil - utilisateur non trouvé")
    void testUpdateUserProfileNotFound() {
        // Given
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(999L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ArticlaException exception = assertThrows(ArticlaException.class,
            () -> userService.updateUserProfile(profileDTO));
        assertEquals("USER_NOT_FOUND", exception.getMessage());
    }
    
}
