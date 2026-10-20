package com.voixdesagesse.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    static final int MAX_FAILURES = 5;
    static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private record Attempts(int failures, Instant firstFailure) {
    }

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptService() {
        this(Clock.systemUTC());
    }

    LoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    public boolean isBlocked(String key) {
        Attempts current = attempts.get(key);
        return current != null && !isExpired(current) && current.failures() >= MAX_FAILURES;
    }

    public void loginFailed(String key) {
        attempts.compute(key, (k, current) -> current == null || isExpired(current)
                ? new Attempts(1, clock.instant())
                : new Attempts(current.failures() + 1, current.firstFailure()));
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void removeExpired() {
        attempts.values().removeIf(this::isExpired);
    }

    private boolean isExpired(Attempts current) {
        return current.firstFailure().plus(BLOCK_DURATION).isBefore(clock.instant());
    }
}
