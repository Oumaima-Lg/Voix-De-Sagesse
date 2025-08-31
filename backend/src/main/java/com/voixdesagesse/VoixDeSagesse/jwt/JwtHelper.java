package com.voixdesagesse.VoixDeSagesse.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

    // JWT secret key - should be stored in application.properties
    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    // JWT expiration time in milliseconds (24 hours)
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    // Generate secret key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a specific claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) throws IllegalArgumentException {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Generate token with username only
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Generate token with custom claims
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return createToken(extraClaims, username);
    }

    // Generate token for UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        claims.put("id", customUserDetails.getId());
        claims.put("nom", customUserDetails.getNom());
        claims.put("prenom", customUserDetails.getPrenom());
        claims.put("profilePicture", customUserDetails.getProfilePicture());
        claims.put("accountType", customUserDetails.getAccountType());
        
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    // Create JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token against UserDetails
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Validate token (basic validation)
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Extract token from Authorization header
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Get remaining validity time in milliseconds
    public Long getTokenValidityTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }

    // Check if token can be refreshed (not expired for too long)
    public Boolean canTokenBeRefreshed(String token) {
        try {
            Date expiration = extractExpiration(token);
            // Allow refresh within 7 days after expiration
            Date maxRefreshDate = new Date(expiration.getTime() + (7 * 24 * 60 * 60 * 1000));
            return new Date().before(maxRefreshDate);
        } catch (Exception e) {
            return false;
        }
    }

    // Refresh token (generate new token with same claims but new expiration)
    public String refreshToken(String token) throws IllegalArgumentException {
        final Claims claims = extractAllClaims(token);
        claims.remove(Claims.ISSUED_AT);
        claims.remove(Claims.EXPIRATION);
        
        return createToken(claims, claims.getSubject());
    }
}