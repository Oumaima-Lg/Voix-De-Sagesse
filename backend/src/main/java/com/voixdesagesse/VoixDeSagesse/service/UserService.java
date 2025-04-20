package com.voixdesagesse.VoixDeSagesse.service;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
// import com.voixdesagesse.VoixDeSagesse.entity.User;
// import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;

import jakarta.validation.Valid;

// import java.util.List;
// import java.util.Optional;

// @Service
public interface UserService {

    public UserDTO registerUser(UserDTO userDTO) throws ArticlaException;

    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException;



    // @Autowired
    // private UserRepository userRepository;

    // // Créer un utilisateur
    // public User createUser(User user) {
    //     return userRepository.save(user);
    // }

    // // Trouver un utilisateur par son ID
    // public Optional<User> getUserById(String id) {
    //     return userRepository.findById(id);
    // }

    // // Trouver un utilisateur par son email
    // public User getUserByEmail(String email) {
    //     return userRepository.findByEmail(email);
    // }

    // // Mettre à jour un utilisateur
    // public User updateUser(String id, User updatedUser) {
    //     if (userRepository.existsById(id)) {
    //         updatedUser.setId(id);
    //         return userRepository.save(updatedUser);
    //     }
    //     return null; // Si l'utilisateur n'existe pas
    // }

    // // Supprimer un utilisateur
    // public void deleteUser(String id) {
    //     userRepository.deleteById(id);
    // }

    // // Lister tous les utilisateurs
    // public List<User> getAllUsers() {
    //     return userRepository.findAll();
    // }
}
