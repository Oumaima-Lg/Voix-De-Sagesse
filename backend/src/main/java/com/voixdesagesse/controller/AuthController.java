package com.voixdesagesse.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.OtpRequest;
import com.voixdesagesse.dto.OtpVerificationResponse;
import com.voixdesagesse.dto.ResetPasswordRequest;
import com.voixdesagesse.dto.ResponseDTO;
import com.voixdesagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.dto.VerifyOtpRequest;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.dto.AuthenticationRequest;
import com.voixdesagesse.dto.AuthenticationResponse;
import com.voixdesagesse.security.JwtHelper;
import com.voixdesagesse.security.LoginAttemptService;
import com.voixdesagesse.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Authentification")
// Public routes: no JWT needed.
@SecurityRequirements
@RestController
@Validated
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final UserDetailsService userDetailsService;
    
    private final AuthenticationManager authenticationManager;

    private final JwtHelper jwtHelper;

    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @Operation(summary = "Se connecter : renvoie un jeton JWT")
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthenticationRequest request,
            HttpServletRequest httpRequest) throws VoixDeSagesseException {
        String attemptKey = request.getEmail().toLowerCase() + "|" + httpRequest.getRemoteAddr();
        if (loginAttemptService.isBlocked(attemptKey))
            throw new VoixDeSagesseException("LOGIN_BLOCKED");

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            loginAttemptService.loginSucceeded(attemptKey);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            final String jwt = jwtHelper.generateToken(userDetails);
            
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
            
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(attemptKey);
            log.warn("Failed login attempt from {}", httpRequest.getRemoteAddr());
            throw new VoixDeSagesseException("INVALID_CREDENTIALS");
        }
    }

    // Step 1: emails a code. Nothing is created in `users` yet.
    @Operation(summary = "Inscription, étape 1 : envoie un code par email")
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserRegistrationDTO registerDTO)
            throws VoixDeSagesseException {
        return new ResponseEntity<>(userService.startRegistration(registerDTO), HttpStatus.ACCEPTED);
    }

    // Step 2: the code proves the address belongs to the user; the account is created.
    @Operation(summary = "Inscription, étape 2 : vérifie le code et crée le compte")
    @PostMapping("/register/verify")
    public ResponseEntity<UserRegistrationDTO> confirmRegistration(@RequestBody @Valid VerifyOtpRequest request)
            throws VoixDeSagesseException {
        return new ResponseEntity<>(userService.confirmRegistration(request.email(), request.otp()), HttpStatus.CREATED);
    }

    @Operation(summary = "Mot de passe oublié, étape 3 : enregistre le nouveau mot de passe")
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid ResetPasswordRequest request)
            throws VoixDeSagesseException {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @Operation(summary = "Mot de passe oublié, étape 1 : envoie un code par email")
    @PostMapping("/password/otp")
    public ResponseEntity<ResponseDTO> sendOtp(@RequestBody @Valid OtpRequest request) throws VoixDeSagesseException {
        userService.sendOtp(request.email());
        return ResponseEntity.ok(new ResponseDTO("Si un compte existe avec cet email, un code vient d'être envoyé."));
    }

    // POST so the code never ends up in URLs or access logs.
    @Operation(summary = "Mot de passe oublié, étape 2 : vérifie le code")
    @PostMapping("/password/otp/verify")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request)
            throws VoixDeSagesseException {
        String resetToken = userService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(new OtpVerificationResponse("Code vérifié.", resetToken));
    }


    
}