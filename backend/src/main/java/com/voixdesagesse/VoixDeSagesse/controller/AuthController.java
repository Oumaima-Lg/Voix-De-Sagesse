package com.voixdesagesse.VoixDeSagesse.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.jwt.AuthenticationRequest;
import com.voixdesagesse.VoixDeSagesse.jwt.AuthenticationResponse;
import com.voixdesagesse.VoixDeSagesse.jwt.JwtHelper;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final UserDetailsService userDetailsService;
    
    private final AuthenticationManager authenticationManager;

    private final JwtHelper jwtHelper;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        try {
            log.info("Attempting authentication for email: {}", request.getEmail());
            
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            final String jwt = jwtHelper.generateToken(userDetails);
            
            log.info("Authentication successful for email: {}", request.getEmail());
            
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
            
        } catch (BadCredentialsException e) {
            log.error("Authentication failed - Bad credentials for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
        } catch (AuthenticationException e) {
            log.error("Authentication error for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Authentication failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDTO> registerUser(@RequestBody @Valid UserRegistrationDTO registerDTO)
            throws ArticlaException {

        return new ResponseEntity<>(userService.registerUser(registerDTO), HttpStatus.CREATED);
    }

    @PostMapping("/changePass")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid LoginDTO loginDTO) throws ArticlaException {

        return new ResponseEntity<>(userService.changePassword(loginDTO), HttpStatus.OK);
    }

    @PostMapping("/sendOtp/{email}")
    public ResponseEntity<ResponseDTO> sendOtp(@PathVariable @Email(message = "{user.email.invalid}") String email)
            throws Exception {
        userService.sendOtp(email);
        return new ResponseEntity<>(new ResponseDTO("OTP sent successfully."), HttpStatus.OK);
    }

    @GetMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<ResponseDTO> verifyOtp(@PathVariable @Email(message = "{user.email.invalid}") String email,
            @Pattern(regexp = "^[0-9]{6}$", message = "{otp.invalid}") @PathVariable String otp)
            throws ArticlaException {
        userService.verifyOtp(email, otp);
        return new ResponseEntity<>(new ResponseDTO("OTP has been verified."), HttpStatus.OK);
    }


    
}