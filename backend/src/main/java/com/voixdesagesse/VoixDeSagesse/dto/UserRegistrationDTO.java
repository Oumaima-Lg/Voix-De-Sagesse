package com.voixdesagesse.VoixDeSagesse.dto;

import com.voixdesagesse.VoixDeSagesse.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    
    private Long id; 

    @NotBlank(message = "{user.lastname.absent}")
    private String nom;

    @NotBlank(message = "{user.firstname.absent}")
    private String prenom;

    @Email(message = "{user.email.absent}")
    private String email;

    @NotBlank(message = "{user.password.absent}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "{user.password.invalid}"
    )
    private String motdepasse;
    private AccountType accountType;

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setNom(this.nom);
        user.setPrenom(this.prenom);
        user.setEmail(this.email);
        user.setMotdepasse(this.motdepasse);
        user.setAccountType(this.accountType);
        return user;
    }
    
}

// password validation regex for uppercase lowercase special character and number and should have lenght between 8 to 15


// {
//     "nom" : "oooo",
//     "prenom" :  "oooo",
//     "email" : "ooo@gmail.com",
//     "motdepasse" : "oooo123m@",
//     "accountType" : "USER"
// }
