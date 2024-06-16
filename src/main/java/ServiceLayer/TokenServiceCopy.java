// package ServiceLayer;

// import java.util.Date;
// import java.util.Random;

// import io.jsonwebtoken.SignatureAlgorithm;
// import java.util.UUID;
// import java.util.function.Function;

// import javax.crypto.SecretKey;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.JwtException;
// import io.jsonwebtoken.Jwts;

// // this class is responsible for generating tokens for the users in the system
// // and validating the tokens
// // and extracting the information from the token - if this is a guest or a user in the system for example
// @Service
// public class TokenServiceCopy {
//     @Value("${jwk.secret}")
//     private String secret;

//     private final long expirationTime = 1000 * 60 * 60 * 24;
//     private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

//     private static TokenService _instance;

//     public static synchronized TokenService getTokenService() {
//         if (_instance == null) {
//             _instance = new TokenService();
//         }
//         return _instance;
//     }

//     // this function recieves a username and generates a token for the user in the
//     // system
//     public String generateUserToken(String username) {
//         return "testUserToken";
//     }

//     // this function generates a token for a guest is the system
//     public String generateGuestToken() {
//         return "testGuestToken";
//     }

//     public String extractUsername(String token) {
//         // Check if the token is for a test user or guest
//         // if (token.equals("testUserToken")) {
//         //     return appendRandomDigit("testUser");
//         // }
//         // if (token.equals("testGuestToken")) {
//         //     return appendRandomDigit("testGuest");
//         // }
//         // // If the token is not recognized, return null
//         // else {
//         //     return null;
//         // }
//         return appendRandomDigit("testUser");
//     }

//     private String appendRandomDigit(String username) {
//         // Generate a random digit (0-9)
//         Random random = new Random();
//         int randomDigit = random.nextInt(10);

//         // Append the random digit to the username
//         return username + randomDigit;
//     }

//     // public Date extractExpiration(String token) {
//     //     return extractClaim(token, Claims::getExpiration);
//     // }

//     public String extractGuestId(String token) {
//         return "testGuestId";
//     }

//     // private <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver) {
//     //     final Claims claims = extractAllClaims(token);
//     //     return ClaimsResolver.apply(claims);
//     // }

//     // private Claims extractAllClaims(String token) {
//     //     return Jwts.parserBuilder()
//     //             .setSigningKey(key)
//     //             .build()
//     //             .parseClaimsJws(token)
//     //             .getBody();
//     // }

//     // // this function validates the token
//     // public boolean validateToken(String token) {
//     //     try {
//     //         System.out.println("Server is checking Token: "+token);
//     //         Jwts.parserBuilder()
//     //                 .setSigningKey(key)
//     //                 .build()
//     //                 .parseClaimsJws(token);
//     //         return true;
//     //     } catch (JwtException | IllegalArgumentException e) {
//     //         return false;
//     //     }
//     // }

//     // check according to the token if this is a user in the system, and the user is
//     // logged in
//     public boolean isUserAndLoggedIn(String token) {
//         return extractUsername(token) != null;
//     }

//     // change the check in the function
//     public boolean isGuest(String token) {
//         return extractGuestId(token) != null;
//     }
// }