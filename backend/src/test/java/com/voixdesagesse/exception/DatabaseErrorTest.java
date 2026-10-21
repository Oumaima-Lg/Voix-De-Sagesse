package com.voixdesagesse.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.voixdesagesse.controller.CustomErrorController;

import jakarta.servlet.RequestDispatcher;

class DatabaseErrorTest {

    private static final DataAccessResourceFailureException UNREACHABLE = new DataAccessResourceFailureException("Timed out",
            new MongoTimeoutException("Timed out after 30000 ms"));

    @Test
    @DisplayName("Erreurs de base : quota dépassé, base injoignable, autres erreurs")
    void recognizesDatabaseErrors() {
        assertEquals("DATABASE_FULL", GlobalExceptionHandler.databaseErrorKey(
                new RuntimeException("Write failed", new MongoException(8000, "you are over your space quota, using 513 MB of 512 MB"))));
        assertEquals("DATABASE_UNAVAILABLE", GlobalExceptionHandler.databaseErrorKey(
                new InternalAuthenticationServiceException("Login failed", UNREACHABLE)));
        assertNull(GlobalExceptionHandler.databaseErrorKey(new IllegalStateException("Something else")));
    }

    @Test
    @DisplayName("Une base injoignable pendant la lecture du jeton donne aussi le message clair")
    void errorPageExplainsUnreachableDatabase() {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("messages");
        messages.setDefaultEncoding("UTF-8");
        CustomErrorController controller = new CustomErrorController(new GlobalExceptionHandler(messages));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, UNREACHABLE);

        ResponseEntity<ErrorInfo> response = controller.handleError(request);

        assertEquals(503, response.getStatusCode().value());
        assertEquals("La base de données ne répond pas pour le moment. Réessayez dans quelques minutes.",
                response.getBody().getErrorMessage());
    }
}
