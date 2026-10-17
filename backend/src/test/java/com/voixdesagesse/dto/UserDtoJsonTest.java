package com.voixdesagesse.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class UserDtoJsonTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("Le hash du mot de passe n'est jamais sérialisé dans les réponses")
    void passwordIsNeverSerialized() throws Exception {
        UserRegistrationDTO registration = new UserRegistrationDTO(1L, "Nom", "Prenom", "a@b.com", "$2a$10$hash", AccountType.USER);
        UserDTO user = new UserDTO();
        user.setEmail("a@b.com");
        user.setMotdepasse("$2a$10$hash");

        assertFalse(mapper.writeValueAsString(registration).contains("motdepasse"));
        assertFalse(mapper.writeValueAsString(user).contains("motdepasse"));
    }

    @Test
    @DisplayName("Le rôle envoyé par le client à l'inscription est ignoré")
    void accountTypeIsNotReadFromRequest() throws Exception {
        String body = "{\"nom\":\"N\",\"prenom\":\"P\",\"email\":\"a@b.com\",\"motdepasse\":\"Secret12!\",\"accountType\":\"ADMIN\"}";

        UserRegistrationDTO dto = mapper.readValue(body, UserRegistrationDTO.class);

        assertNull(dto.getAccountType());
        assertEquals("Secret12!", dto.getMotdepasse());
    }
}
