package com.voixdesagesse.VoixDeSagesse.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.OtpVerificationResponse;
import com.voixdesagesse.VoixDeSagesse.dto.ResetPasswordRequest;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.dto.VerifyOtpRequest;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.jwt.AuthenticationRequest;
import com.voixdesagesse.VoixDeSagesse.jwt.AuthenticationResponse;
import com.voixdesagesse.VoixDeSagesse.jwt.JwtHelper;
import com.voixdesagesse.VoixDeSagesse.service.LoginAttemptService;
import com.voixdesagesse.VoixDeSagesse.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
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

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthenticationRequest request,
            HttpServletRequest httpRequest) throws ArticlaException {
        String attemptKey = request.getEmail().toLowerCase() + "|" + httpRequest.getRemoteAddr();
        if (loginAttemptService.isBlocked(attemptKey))
            throw new ArticlaException("LOGIN_BLOCKED");

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
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid ResetPasswordRequest request)
            throws ArticlaException {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PostMapping("/sendOtp/{email}")
    public ResponseEntity<ResponseDTO> sendOtp(@PathVariable @Email(message = "{user.email.invalid}") String email)
            throws Exception {
        userService.sendOtp(email);
        return ResponseEntity.ok(new ResponseDTO("Si un compte existe avec cet email, un code vient d'être envoyé."));
    }

    // POST so the code never ends up in URLs or access logs.
    @PostMapping("/verifyOtp")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request)
            throws ArticlaException {
        String resetToken = userService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(new OtpVerificationResponse("Code vérifié.", resetToken));
    }


    
}