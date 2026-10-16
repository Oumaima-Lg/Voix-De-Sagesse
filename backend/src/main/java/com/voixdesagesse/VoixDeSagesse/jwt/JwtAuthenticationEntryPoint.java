package com.voixdesagesse.VoixDeSagesse.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("Unauthorized access attempt to {} from IP: {} - Reason: {}", 
                request.getRequestURI(), 
                request.getRemoteAddr(),
                authException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
            "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"status\": 401, \"timestamp\": \"%s\", \"path\": \"%s\"}", 
            authException.getMessage(),
            java.time.Instant.now().toString(),
            request.getRequestURI()
        );
        
        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}