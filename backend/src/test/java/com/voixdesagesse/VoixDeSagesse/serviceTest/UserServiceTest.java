package com.voixdesagesse.VoixDeSagesse.serviceTest;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.OTPRepository;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import com.voixdesagesse.VoixDeSagesse.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Test
    void firstTest() {
        System.out.print("first test --------------");
    }

    @Test
    void loginUserTest() throws ArticlaException {
        System.out.print("Second test --------------");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("Admin123@gmail.com");
        loginDTO.setMotdepasse("Admin123@");

        // Créer un faux utilisateur
        User user = new User();
        user.setEmail("Admin123@gmail.com");
        user.setMotdepasse("encodedPassword");

        // Simuler le comportement du repository et du passwordEncoder
        when(userRepository.findByEmail("Admin123@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Admin123@", "encodedPassword")).thenReturn(true);

        // Appel du service
        userServiceImpl.loginUser(loginDTO);
    }

}
