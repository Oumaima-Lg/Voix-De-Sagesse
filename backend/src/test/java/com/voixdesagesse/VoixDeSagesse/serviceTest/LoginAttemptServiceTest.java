package com.voixdesagesse.VoixDeSagesse.serviceTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.voixdesagesse.VoixDeSagesse.service.LoginAttemptService;

class LoginAttemptServiceTest {

    private static final String KEY = "alice@test.local|127.0.0.1";

    private MutableClock clock;
    private LoginAttemptService service;

    @BeforeEach
    void setUp() throws Exception {
        clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"));
        Constructor<LoginAttemptService> constructor = LoginAttemptService.class.getDeclaredConstructor(Clock.class);
        constructor.setAccessible(true);
        service = constructor.newInstance(clock);
    }

    @Test
    @DisplayName("Bloqué après 5 échecs")
    void blockedAfterFiveFailures() {
        for (int i = 0; i < 4; i++) service.loginFailed(KEY);
        assertFalse(service.isBlocked(KEY));

        service.loginFailed(KEY);
        assertTrue(service.isBlocked(KEY));
    }

    @Test
    @DisplayName("Débloqué après 15 minutes")
    void unblockedAfterFifteenMinutes() {
        for (int i = 0; i < 5; i++) service.loginFailed(KEY);

        clock.advance(Duration.ofMinutes(16));

        assertFalse(service.isBlocked(KEY));
    }

    @Test
    @DisplayName("Une connexion réussie remet le compteur à zéro")
    void successResetsCounter() {
        for (int i = 0; i < 4; i++) service.loginFailed(KEY);
        service.loginSucceeded(KEY);
        service.loginFailed(KEY);

        assertFalse(service.isBlocked(KEY));
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }
    }
}
