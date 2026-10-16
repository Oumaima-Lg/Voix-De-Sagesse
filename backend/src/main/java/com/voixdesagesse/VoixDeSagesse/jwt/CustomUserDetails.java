
package com.voixdesagesse.VoixDeSagesse.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.voixdesagesse.VoixDeSagesse.dto.AccountType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public CustomUserDetails(Long id, String nom, String prenom, String profilePicture, 
                           String username, String password, AccountType accountType) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.profilePicture = profilePicture;
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.authorities = getAuthoritiesForAccountType(accountType);
    }
    
    public CustomUserDetails(Long id, String nom, String prenom, String profilePicture, 
                           String username, String password, AccountType accountType,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.profilePicture = profilePicture;
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        
        // If authorities are provided, use them, otherwise generate from account type
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities = authorities;
        } else {
            this.authorities = getAuthoritiesForAccountType(accountType);
        }
    }
    
    private Collection<GrantedAuthority> getAuthoritiesForAccountType(AccountType accountType) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        
        if (accountType == AccountType.ADMIN) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}