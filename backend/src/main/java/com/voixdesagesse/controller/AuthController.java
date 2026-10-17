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

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDTO> registerUser(@RequestBody @Valid UserRegistrationDTO registerDTO)
            throws VoixDeSagesseException {

        return new ResponseEntity<>(userService.registerUser(registerDTO), HttpStatus.CREATED);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid ResetPasswordRequest request)
            throws VoixDeSagesseException {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PostMapping("/password/otp")
    public ResponseEntity<ResponseDTO> sendOtp(@RequestBody @Valid OtpRequest request) throws Exception {
        userService.sendOtp(request.email());
        return ResponseEntity.ok(new ResponseDTO("Si un compte existe avec cet email, un code vient d'être envoyé."));
    }

    // POST so the code never ends up in URLs or access logs.
    @PostMapping("/password/otp/verify")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request)
            throws VoixDeSagesseException {
        String resetToken = userService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(new OtpVerificationResponse("Code vérifié.", resetToken));
    }


    
}