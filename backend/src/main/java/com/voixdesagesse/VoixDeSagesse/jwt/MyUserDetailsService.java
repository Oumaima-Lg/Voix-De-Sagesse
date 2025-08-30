package com.voixdesagesse.VoixDeSagesse.jwt;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserDTO dto = userService.getUserByEmail(email);
            if (dto == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return new CustomUserDetails(
                dto.getId(), 
                dto.getNom(),
                dto.getPrenom(),
                dto.getProfilePicture(),
                dto.getEmail(), 
                dto.getMotdepasse(),
                dto.getAccountType(), 
                new ArrayList<>()
            );
        } catch (ArticlaException e) {
            e.printStackTrace();
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}