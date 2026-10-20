package com.voixdesagesse.exception;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Map<String, HttpStatus> STATUS_BY_KEY = Map.ofEntries(
            Map.entry("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
            Map.entry("COMMENT_DELETE_FORBIDDEN", HttpStatus.FORBIDDEN),
            Map.entry("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
            Map.entry("ARTICLE_NOT_FOUND", HttpStatus.NOT_FOUND),
            Map.entry("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND),
            Map.entry("SIGNAL_NOT_FOUND", HttpStatus.NOT_FOUND),
            Map.entry("USER_FOUND", HttpStatus.CONFLICT),
            Map.entry("ALREADY_LIKED", HttpStatus.CONFLICT),
            Map.entry("NOT_LIKED", HttpStatus.CONFLICT),
            Map.entry("ALREADY_SAVED", HttpStatus.CONFLICT),
            Map.entry("NOT_SAVED", HttpStatus.CONFLICT),
            Map.entry("ALREADY_FOLLOWING", HttpStatus.CONFLICT),
            Map.entry("NOT_FOLLOWING", HttpStatus.CONFLICT),
            Map.entry("ALREADY_REPORTED", HttpStatus.CONFLICT),
            Map.entry("SIGNAL_ALREADY_PROCESSED", HttpStatus.CONFLICT),
            Map.entry("OTP_TOO_SOON", HttpStatus.TOO_MANY_REQUESTS),
            Map.entry("LOGIN_BLOCKED", HttpStatus.TOO_MANY_REQUESTS),
            Map.entry("MAIL_FAILED", HttpStatus.SERVICE_UNAVAILABLE));

    private final MessageSource messageSource;

    @ExceptionHandler(VoixDeSagesseException.class)
    public ResponseEntity<ErrorInfo> handleArticlaException(VoixDeSagesseException exception) {
        String key = exception.getMessage();
        String msg = messageSource.getMessage(key, null, key, Locale.FRENCH);
        return error(STATUS_BY_KEY.getOrDefault(key, HttpStatus.BAD_REQUEST), msg);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
    public ResponseEntity<ErrorInfo> validatorExceptionHandler(Exception exception) {
        String msg = "";
        if (exception instanceof MethodArgumentNotValidException manvException) {
            msg = manvException.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else {
            ConstraintViolationException cvException = (ConstraintViolationException) exception;
            msg = cvException.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
        }
        return error(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, TypeMismatchException.class })
    public ResponseEntity<ErrorInfo> handleUnreadableRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Requête invalide.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorInfo> handleBadCredentialsException(BadCredentialsException exception) {
        return error(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> handleAccessDenied(AccessDeniedException exception) {
        return error(HttpStatus.FORBIDDEN, "Accès refusé.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorInfo> handleUploadTooLarge(MaxUploadSizeExceededException exception) {
        return error(HttpStatus.CONTENT_TOO_LARGE, "Le fichier ne doit pas dépasser 5 Mo.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleUnexpected(Exception exception) {
        if (exception instanceof ErrorResponse errorResponse && errorResponse.getStatusCode().is4xxClientError()) {
            HttpStatusCode status = errorResponse.getStatusCode();
            return error(status, status.value() == 404 ? "Ressource introuvable." : "Requête invalide.");
        }
        log.error("Unexpected error", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.");
    }

    private static ResponseEntity<ErrorInfo> error(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(new ErrorInfo(message, status.value(), LocalDateTime.now()));
    }
}
