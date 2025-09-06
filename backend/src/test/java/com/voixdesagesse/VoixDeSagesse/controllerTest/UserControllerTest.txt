package com.voixdesagesse.VoixDeSagesse.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.dto.AccountType;
import com.voixdesagesse.VoixDeSagesse.service.UserService;
import com.voixdesagesse.VoixDeSagesse.controller.UserController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_ValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        UserRegistrationDTO inputDTO = new UserRegistrationDTO(
                null,
                "Dupont",
                "Jean",
                "jean.dupont@email.com",
                "Password123!",
                AccountType.USER
        );

        UserRegistrationDTO responseDTO = new UserRegistrationDTO(
                1L,
                "Dupont",
                "Jean",
                "jean.dupont@email.com",
                "Password123!",
                AccountType.USER
        );

        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"))
                .andExpect(jsonPath("$.accountType").value("USER"));
    }

//    
    @Test
    void registerUser_NullFields_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRegistrationDTO inputDTO = new UserRegistrationDTO(
                null,
                null,  // nom null
                null,  // prenom null
                null,  // email null
                null,  // mot de passe null
                AccountType.USER
        );

        // Act & Assert
        mockMvc.perform(post("/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidPasswordVariations_ShouldReturnBadRequest() throws Exception {
        // Test avec différentes variations de mots de passe invalides
        String[] invalidPasswords = {
                "password",         // pas de majuscule, chiffre, caractère spécial
                "PASSWORD123!",     // pas de minuscule
                "Password!",        // pas de chiffre
                "Password123",      // pas de caractère spécial
                "Pass1!",          // trop court
                "Password123!TooLongForValidation"  // trop long
        };

        for (String password : invalidPasswords) {
            UserRegistrationDTO inputDTO = new UserRegistrationDTO(
                    null,
                    "Dupont",
                    "Jean",
                    "jean.dupont@email.com",
                    password,
                    AccountType.USER
            );

            mockMvc.perform(post("/users/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isBadRequest());
        }
    }
}