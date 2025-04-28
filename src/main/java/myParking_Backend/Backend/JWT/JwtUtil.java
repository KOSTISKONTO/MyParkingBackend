package myParking_Backend.Backend.JWT;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

    // Διαβάζουμε το μυστικό κλειδί από το application.properties
    @Value("${security.jwt.secret-key}")
    private String secretKey;  // Αποθήκευση του JWT Secret

    private Key getSigningKey() {
        // Χρησιμοποιούμε την τιμή του jwt.secret για να δημιουργήσουμε το Key
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 λεπτά ισχύς
                .signWith(getSigningKey())  // Υπογραφή με το secret key
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Χρησιμοποιούμε το signing key για το parsing
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())  // Χρησιμοποιούμε το signing key για το parsing
                    .build()
                    .parseClaimsJws(token);
            return true; // Αν δεν πετάξει exception, το token είναι έγκυρο
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false; // Αν πετάξει exception, το token δεν είναι έγκυρο
    }
}
