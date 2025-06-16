package com.voixdesagesse.VoixDeSagesse.dto;

import java.sql.Date;
import java.util.List;


import com.voixdesagesse.VoixDeSagesse.entity.Article;
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
public class UserDTO {
    
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

    private Date dateInscription;
    private String role;
    private String bio;
    private AccountType accountType;
    private List<Article> articles; 

    public User toEntity() {
        return new User(this.id, this.nom, this.prenom, this.email, this.motdepasse, this.dateInscription, this.role, this.bio, this.accountType ,this.articles);
    }
    
}

// password validation regex for uppercase lowercase special character and number and should have lenght between 8 to 15
// 3:36:57