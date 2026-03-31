package in.bawvpl.Authify.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ Secret key (must be >= 32 chars for HS256)
    private static final String SECRET =
            "authify-super-secure-jwt-secret-key-256-bit-minimum";

    // ✅ Token expiry (24 hours)
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    // ✅ Signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ MAIN METHOD (Fix for your error)
    public String generateAccessToken(String email) {
        return generateToken(email);
    }

    // ✅ CORE TOKEN GENERATOR
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract username (email)
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ Validate token
    public boolean validateToken(String token, String username) {
        try {
            return username.equals(extractUsername(token))
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ Check expiration
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // ✅ Extract all claims (clean reusable method)
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}