package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.repository.PendingRegistrationRepository;
import com.voixdesagesse.repository.UserRepository;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/** An account only exists once the code sent by email has been confirmed. */
@SpringBootTest
@AutoConfigureMockMvc
class RegistrationIntegrationTest {

    private static final String EMAIL = "new-user@test.local";
    private static final String PASSWORD = "Password1!";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PendingRegistrationRepository pendingRegistrationRepository;
    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        userRepository.findByEmail(EMAIL).ifPresent(userRepository::delete);
        pendingRegistrationRepository.deleteAll();
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    @DisplayName("Inscription : le compte n'est créé qu'après le bon code")
    void accountIsCreatedOnlyWithTheEmailedCode() throws Exception {
        register().andExpect(status().isAccepted());
        verify(mailSender).send(any(MimeMessage.class));
        assertFalse(userRepository.findByEmail(EMAIL).isPresent());
        login().andExpect(status().isUnauthorized());

        String code = pendingRegistrationRepository.findById(EMAIL).orElseThrow().getOtpCode();
        String wrongCode = code.equals("000000") ? "111111" : "000000";
        confirm(wrongCode).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorMessage").value("Code incorrect."));
        assertFalse(userRepository.findByEmail(EMAIL).isPresent());

        confirm(code).andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.motdepasse").doesNotExist());
        assertEquals(AccountType.USER, userRepository.findByEmail(EMAIL).orElseThrow().getAccountType());
        login().andExpect(status().isOk()).andExpect(jsonPath("$.jwt").exists());

        // The code is single-use.
        confirm(code).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Inscription : un nouveau code ne peut être demandé qu'après une minute")
    void codeCannotBeResentRightAway() throws Exception {
        register().andExpect(status().isAccepted());
        register().andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Inscription : si l'email ne part pas, l'utilisateur peut réessayer tout de suite")
    void mailFailureCanBeRetried() throws Exception {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(MimeMessage.class));
        register().andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.errorMessage").value("L'email n'a pas pu être envoyé. Réessayez dans quelques minutes."));
        assertTrue(pendingRegistrationRepository.findById(EMAIL).isEmpty());
    }

    private ResultActions register() throws Exception {
        return mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(
            "{\"nom\":\"Nouveau\",\"prenom\":\"Compte\",\"email\":\"" + EMAIL + "\",\"motdepasse\":\"" + PASSWORD + "\"}"));
    }

    private ResultActions confirm(String code) throws Exception {
        return mvc.perform(post("/auth/register/verify").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + EMAIL + "\",\"otp\":\"" + code + "\"}"));
    }

    private ResultActions login() throws Exception {
        return mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + EMAIL + "\",\"password\":\"" + PASSWORD + "\"}"));
    }
}
