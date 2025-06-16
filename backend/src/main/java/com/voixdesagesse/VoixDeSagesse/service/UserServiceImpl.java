package com.voixdesagesse.VoixDeSagesse.service;

import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import org.springframework.stereotype.Service;
import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(UserDTO userDTO) throws ArticlaException {

        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if(optional.isPresent()) throw new ArticlaException("USER_FOUND");

        userDTO.setId(Utilities.getNextSequence("users"));
        userDTO.setMotdepasse(passwordEncoder.encode(userDTO.getMotdepasse()));

        User user = userDTO.toEntity(); 
        user = userRepository.save(user);
        
        return user.toDTO(); 
    }
    
    @Override
    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException {

        User user = userRepository.findByEmail(loginDTO.getEmail())
            .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        
        if (!passwordEncoder.matches(loginDTO.getMotdepasse(), user.getMotdepasse()))
            throw new ArticlaException("INVALID_CREDENTIALS");
        
        return user.toDTO();
    }

}
