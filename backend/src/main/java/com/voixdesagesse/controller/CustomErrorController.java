package com.voixdesagesse.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.exception.ErrorInfo;
import com.voixdesagesse.exception.GlobalExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private static final Map<Integer, String> MESSAGES = Map.of(
            400, "Requête invalide.",
            401, "Authentification requise.",
            403, "Accès refusé.",
            404, "Ressource introuvable.",
            405, "Méthode non autorisée.");

    private final GlobalExceptionHandler exceptionHandler;

    @RequestMapping("/error")
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest request) {
        if (request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Exception exception
                && GlobalExceptionHandler.databaseErrorKey(exception) != null)
            return exceptionHandler.handleUnexpected(exception);
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = code != null ? Integer.parseInt(code.toString()) : 500;
        String message = MESSAGES.getOrDefault(status,
                status >= 500 ? "Une erreur interne est survenue." : "Requête invalide.");
        return ResponseEntity.status(status).body(new ErrorInfo(message, status, LocalDateTime.now()));
    }
}
