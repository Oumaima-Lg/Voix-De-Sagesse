package com.voixdesagesse.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.exception.ErrorInfo;

import io.swagger.v3.oas.annotations.Hidden;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Errors raised outside the controllers (for example a 403 from Spring Security) are forwarded here.
 * They get the same JSON shape as the errors handled by GlobalExceptionHandler.
 */
@Hidden
@RestController
public class CustomErrorController implements ErrorController {

    private static final Map<Integer, String> MESSAGES = Map.of(
            400, "Requête invalide.",
            401, "Authentification requise.",
            403, "Accès refusé.",
            404, "Ressource introuvable.",
            405, "Méthode non autorisée.");

    @RequestMapping("/error")
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest request) {
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = code != null ? Integer.parseInt(code.toString()) : 500;
        String message = MESSAGES.getOrDefault(status,
                status >= 500 ? "Une erreur interne est survenue." : "Requête invalide.");
        return ResponseEntity.status(status).body(new ErrorInfo(message, status, LocalDateTime.now()));
    }
}
