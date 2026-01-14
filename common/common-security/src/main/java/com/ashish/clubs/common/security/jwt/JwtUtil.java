package com.ashish.clubs.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

/**
 * Utility class for JWT token generation, validation, and extraction.
 * Services needing JWT capabilities can use this.
 */
@Component
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey; // Must be at least 256-bit (32 bytes)
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // in milliseconds
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration; // in milliseconds

    /**
     * Decodes the base64 secret key and generates an HMAC-SHA256 SecretKey.
     * This key is used for signing and verifying JWTs.
     *
     * @return SecretKey for JWT operations.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ---- Token Generation -----

    /**
     * Generates a new JWT Access Token for a given username and user roles.
     * The token includes roles in its claims and has a defined expiration.
     *
     * @param username The subject (username) of the token.
     * @param roles A set of roles to include in the token's claims.
     * @return A Mono emitting the generated JWT string.
     */
    public Mono<String> generateAccessToken(String username, Set<String> roles) {
        return buildToken(username, roles, jwtExpiration);
    }

    public Mono<String> generateRefreshToken(String username) {
        // Refresh token typically has fewer claims, often just subject (username) or a JTI
        return buildToken(username, Set.of(), refreshExpiration); // No roles for refresh token usually
    }

    /**
     * Builds a JWT token with specified claims, subject, issue date, and expiration.
     * This method is internal and used by `generateAccessToken`.
     *
     * @param username The subject of the token.
     * @param roles Roles to be included in the claims (typically for access tokens).
     * @param expirationTime The validity duration of the token in milliseconds.
     * @return A Mono emitting the compact JWT string.
     */
    private Mono<String> buildToken(String username, Set<String> roles, long expirationTime) {
        return Mono.fromCallable(() -> {
            Map<String, Object> claims = new HashMap<>();
            if (roles != null && !roles.isEmpty()) {
                claims.put("roles", roles); // Store roles in claims for Access Token
            }

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .claims(claims) // Use claims() for newer JJWT versions
                    .subject(username)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSignInKey(), Jwts.SIG.HS256) // Use Jwts.SIG for newer JJWT versions
                    .compact();
        });
    }

    // --- Token Validation and Extraction ---

    /**
     * Validates a given JWT token against a UserDetails object.
     * Checks if the username in the token matches UserDetails and if the token is not expired.
     *
     * @param token The JWT string to validate.
     * @param userDetails The UserDetails object to compare against.
     * @return A Mono emitting true if the token is valid, false otherwise.
     */
    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return Mono.fromCallable(() -> {
                    final String username = extractUsername(token);
                    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
                })
                .onErrorReturn(false); // If any exception during extraction/validation, return false
    }

    /**
     * Extracts all claims from a JWT token.
     * This is the core method for parsing and verifying the token.
     *
     * @param token The JWT string.
     * @return A Mono emitting the Claims object if parsing is successful.
     * Emits an error Mono if parsing fails (e.g., invalid signature, expired token).
     */
    public Mono<Claims> extractAllClaims(String token) {
        return Mono.fromCallable(() -> {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSignInKey()) // Cast to SecretKey
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }).onErrorResume(e -> {
            // Log specific JWT exceptions for better debugging
            if (e instanceof SignatureException) {
                System.err.println("Invalid JWT signature: " + e.getMessage());
            } else if (e instanceof MalformedJwtException) {
                System.err.println("Invalid JWT token: " + e.getMessage());
            } else if (e instanceof ExpiredJwtException) {
                System.err.println("Expired JWT token: " + e.getMessage());
            } else if (e instanceof UnsupportedJwtException) {
                System.err.println("Unsupported JWT token: " + e.getMessage());
            } else if (e instanceof IllegalArgumentException) {
                System.err.println("JWT claims string is empty: " + e.getMessage());
            } else {
                System.err.println("JWT processing error: " + e.getMessage());
            }
            return Mono.error(e); // Propagate the specific error
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return extractAllClaims(token).map(claimsResolver).block(); // Blocking call here, needs care if used directly
        // Better to chain reactively: extractAllClaims().map(claimsResolver)
    }

    /**
     * Extracts a specific claim from a JWT token reactively.
     *
     * @param token The JWT string.
     * @param claimsResolver A function to extract the desired claim from the Claims object.
     * @param <T> The type of the extracted claim.
     * @return A Mono emitting the extracted claim.
     */
    public <T> Mono<T> extractClaimReactive(String token, Function<Claims, T> claimsResolver) {
        return extractAllClaims(token).map(claimsResolver);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // This will block if not called reactively
    }

    /**
     * Extracts the username (subject) from a JWT token reactively.
     *
     * @param token The JWT string.
     * @return A Mono emitting the username.
     */
    public Mono<String> extractUsernameReactive(String token) {
        return extractClaimReactive(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // This will block if not called reactively
    }

    /**
     * Extracts the expiration date from a JWT token reactively.
     *
     * @param token The JWT string.
     * @return A Mono emitting the expiration Date.
     */
    public Mono<Date> extractExpirationReactive(String token) {
        return extractClaimReactive(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // This will block
    }

    /**
     * Checks if a JWT token is expired reactively.
     *
     * @param token The JWT string.
     * @return A Mono emitting true if the token is expired, false otherwise.
     */
    private Mono<Boolean> isTokenExpiredReactive(String token) {
        return extractExpirationReactive(token).map(expirationDate -> expirationDate.before(new Date()));
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        // Corrected: Directly cast to List and then stream
        List<String> rolesList = extractClaim(token, claims -> (List<String>) claims.get("roles"));
        return new HashSet<>(rolesList); // Convert List to Set
    }

    /**
     * Extracts roles from a JWT token reactively.
     * Assumes roles are stored as a List<String> in the "roles" claim.
     *
     * @param token The JWT string.
     * @return A Mono emitting a Set of roles.
     */
    @SuppressWarnings("unchecked")
    public Mono<Set<String>> getRolesFromTokenReactive(String token) {
    // Corrected: Directly cast to List and then map to a Set
        return extractClaimReactive(token, claims -> (List<String>) claims.get("roles"))
                .map(HashSet::new); // Convert List<String> to HashSet<String>
    }

    /**
     * Returns the configured refresh token expiration time.
     * This value is used by the RefreshTokenService for Redis entry TTL.
     *
     * @return The refresh token expiration time in milliseconds.
     */
    public long getRefreshExpiration() {
        return this.refreshExpiration;
    }
}
