package ServiceLayer;

import java.util.Date;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class TokenService {
    @Value("${jwk.secret}")
    private String secret;

    private final long expirationTime = 1000 * 60 * 60 * 24;
    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateUserToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String generateGuestToken() {
        String guestId = UUID.randomUUID().toString();
        return Jwts.builder()
                .claim("guestId", guestId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractGuestId(String token) {
        return extractClaim(token, claims -> claims.get("guestId", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver){
        final Claims claims = extractAllClaims(token);
        return ClaimsResolver.apply(claims);
    }  

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isLoggedIn(String token){
        return extractUsername(token) != null;
    }

    public boolean isGuest(String token){
        return extractUsername(token) != null;
    }
}