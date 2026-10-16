package com.voixdesagesse.VoixDeSagesse.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        Map<String, Object> errorDetails = new HashMap<>();
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404 -> {
                    errorDetails.put("error", "Ressource non trouvée");
                    errorDetails.put("message", "L'endpoint ou la ressource demandée n'existe pas");
                }
                case 500 -> {
                    errorDetails.put("error", "Erreur interne du serveur");
                    errorDetails.put("message", "Une erreur inattendue s'est produite");
                }
                case 403 -> {
                    errorDetails.put("error", "Accès interdit");
                    errorDetails.put("message", "Vous n'avez pas les permissions nécessaires");
                }
                case 401 -> {
                    errorDetails.put("error", "Non autorisé");
                    errorDetails.put("message", "Authentification requise");
                }
                default -> {
                    errorDetails.put("error", "Erreur HTTP " + statusCode);
                    errorDetails.put("message", errorMessage != null ? errorMessage.toString() : "Une erreur s'est produite");
                }
            }
            
            errorDetails.put("status", statusCode);
            errorDetails.put("path", requestUri);
            errorDetails.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(statusCode).body(errorDetails);
        }

        errorDetails.put("error", "Erreur inconnue");
        errorDetails.put("message", "Une erreur inconnue s'est produite");
        errorDetails.put("status", 500);
        errorDetails.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}