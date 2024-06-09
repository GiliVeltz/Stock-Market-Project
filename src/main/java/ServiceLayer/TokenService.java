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

// this class is responsible for generating tokens for the users in the system
// and validating the tokens
// and extracting the information from the token - if this is a guest or a user in the system for example
public class TokenService {
    @Value("${jwk.secret}")
    private String secret;

    private final long expirationTime = 1000 * 60 * 60 * 24;
    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static TokenService _instance;

    public static synchronized TokenService getTokenService() {
        if (_instance == null) {
            _instance = new TokenService();
        }
        return _instance;
    }

    // this function recieves a username and generates a token for the user in the
    // system
    public String generateUserToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    // this function generates a token for a guest is the system
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

    private <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver) {
        final Claims claims = extractAllClaims(token);
        return ClaimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // this function validates the token
    public boolean validateToken(String token) {
        try {
            System.out.println("Server is checking Token: "+token);
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // check according to the token if this is a user in the system, and the user is
    // logged in
    public boolean isUserAndLoggedIn(String token) {
        return extractUsername(token) != null;
    }

    // change the check in the function
    public boolean isGuest(String token) {
        return extractUsername(token) != null;
    }
}