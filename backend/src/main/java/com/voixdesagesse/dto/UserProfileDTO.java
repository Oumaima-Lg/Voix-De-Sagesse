package com.voixdesagesse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    @NotBlank(message = "{user.lastname.absent}")
    @Size(max = 50, message = "{user.name.too.long}")
    private String nom;
    @NotBlank(message = "{user.firstname.absent}")
    @Size(max = 50, message = "{user.name.too.long}")
    private String prenom;
    private String email;
    private String username;
    @Size(max = 20, message = "{user.phone.too.long}")
    private String phoneNumber;
    @Size(max = 100, message = "{user.location.too.long}")
    private String location;
    @Size(max = 200, message = "{user.website.invalid}")
    @Pattern(regexp = "^$|^https?://\\S+$", message = "{user.website.invalid}")
    private String website;
    private String profilePicture;
    @Size(max = 500, message = "{user.bio.exceeded}" )
    private String bio;

    private Long contentCount;
    private Long followersCount;
    private Long followingCount;
    private Long likesReceived;
    
    
}
