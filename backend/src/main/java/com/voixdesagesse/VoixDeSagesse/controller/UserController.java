package com.voixdesagesse.VoixDeSagesse.controller;

import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;

// import java.util.List;
// import java.util.Optional;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws ArticlaException {
        userDTO = userService.registerUser(userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody @Valid LoginDTO loginDTO) throws ArticlaException {

        return new ResponseEntity<>(userService.loginUser(loginDTO), HttpStatus.OK);
    }

    // // Créer un utilisateur
    // @PostMapping
    // public ResponseEntity<User> createUser(@RequestBody User user) {
    //     User createdUser = userService.createUser(user);
    //     return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    // }

    // // Récupérer un utilisateur par ID
    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable String id) {
    //     Optional<User> user = userService.getUserById(id);
    //     return user.map(ResponseEntity::ok)
    //                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    // }

    // // Lister tous les utilisateurs
    // @GetMapping
    // public List<User> getAllUsers() {
    //     return userService.getAllUsers();
    // }

    // // Mettre à jour un utilisateur
    // @PutMapping("/{id}")
    // public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
    //     User user = userService.updateUser(id, updatedUser);
    //     return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    // }

    // // Supprimer un utilisateur
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    //     userService.deleteUser(id);
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    // }
}
