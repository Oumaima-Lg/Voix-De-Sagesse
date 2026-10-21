package com.voixdesagesse.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.voixdesagesse.exception.VoixDeSagesseException;

@Service
public class RateLimiter {

    public enum Limit {
        EMAIL_CODE("TOO_MANY_CODE_REQUESTS"),
        PICTURE_UPLOAD("TOO_MANY_UPLOADS");

        private final String errorKey;

        Limit(String errorKey) {
            this.errorKey = errorKey;
        }
    }

    static final Duration WINDOW = Duration.ofHours(1);

    private record Window(int count, Instant resetAt) {
    }

    private final Map<Limit, Integer> maxPerHour;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final Clock clock;

    @Autowired
    public RateLimiter(@Value("${app.rate-limit.email-codes-per-hour}") int emailCodesPerHour,
            @Value("${app.rate-limit.picture-uploads-per-hour}") int pictureUploadsPerHour) {
        this(Map.of(Limit.EMAIL_CODE, emailCodesPerHour, Limit.PICTURE_UPLOAD, pictureUploadsPerHour),
                Clock.systemUTC());
    }

    RateLimiter(Map<Limit, Integer> maxPerHour, Clock clock) {
        this.maxPerHour = maxPerHour;
        this.clock = clock;
    }

    public void check(Limit limit, String key) throws VoixDeSagesseException {
        Instant now = clock.instant();
        Window window = windows.compute(limit + "|" + key, (k, current) -> current == null || !now.isBefore(current.resetAt())
                ? new Window(1, now.plus(WINDOW))
                : new Window(current.count() + 1, current.resetAt()));
        if (window.count() > maxPerHour.get(limit))
            throw new VoixDeSagesseException(limit.errorKey);
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void removeExpired() {
        Instant now = clock.instant();
        windows.values().removeIf(window -> !now.isBefore(window.resetAt()));
    }
}
