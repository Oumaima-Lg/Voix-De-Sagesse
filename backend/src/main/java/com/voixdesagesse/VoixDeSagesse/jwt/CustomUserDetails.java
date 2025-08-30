package com.voixdesagesse.VoixDeSagesse.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String nom;
    private String prenom;
    private String profilePicture;
    private String username;
    private String password;
    private AccountType accountType;

 

    private Collection<? extends GrantedAuthority> authorities;

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // public String getPassword() {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // public String getUsername() {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
    
}
