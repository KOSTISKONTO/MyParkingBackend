package myParking_Backend.Backend.config;

import jakarta.servlet.http.HttpServletResponse;
import myParking_Backend.Backend.GlobalExceptions.CustomAuthEntryPoint;
import myParking_Backend.Backend.JWT.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAuthEntryPoint customAuthEntryPoint;

    // Εισάγουμε το JwtTokenFilter μέσω dependency injection
    @Autowired
    public SecurityConfig(JwtTokenFilter jwtTokenFilter, CustomAuthEntryPoint customAuthEntryPoint) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                 .csrf(csrf -> csrf
                        //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Αποθηκεύει το CSRF token σε cookie
                         .disable()
                 )

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless API

                //ΜΕΤΡΑΕΙ Η ΣΕΙΡΑ ->ΤΑ ΠΡΩΤΑ ΕΧΟΥΝ ΠΡΟΤΕΡΑΙΟΤΗΑ
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/test/register").permitAll()  // Επιτρέπει το register χωρίς login
                        .requestMatchers("/booking/distance").permitAll()
                        .requestMatchers("/booking/todo").permitAll()
                        .requestMatchers("/getCustomers").hasRole("ADMIN")
                        .requestMatchers("/getOwners").hasRole("ADMIN")
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register_customer", "/register_owner", "/logout").permitAll()
                        .requestMatchers(("/booking/availability")).permitAll()
                        .requestMatchers(("/booking/newBooking")).permitAll()
                        .requestMatchers("/websocket/**").permitAll()
                        .requestMatchers("/booking/popularcities").permitAll()
                        .requestMatchers("/booking/coordinates").permitAll()
                        .requestMatchers("/updatemyprofile/name").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        .requestMatchers("/updatemyprofile/lastname").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        .requestMatchers("/updatemyprofile/username").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        .requestMatchers("/updatemyprofile/email").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        .requestMatchers("/updatemyprofile/changecredentials").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        .requestMatchers("/parking/cities").permitAll()
                        .requestMatchers("/parking/location").permitAll()
                        .requestMatchers("/parking/getParkings").hasRole("ADMIN")
                        .requestMatchers("/parking/getfilesofparking").hasRole("ADMIN")
                        .requestMatchers("/parking/validate_parking_admin").hasRole("ADMIN")
                        .requestMatchers("/parking/reject_parking_admin").hasAnyRole("ADMIN")
                        .requestMatchers("/booking/**").hasAnyRole("CUSTOMER")
                        .requestMatchers("/parking/**").hasAnyRole("OWNER")
                        .anyRequest().authenticated()  // Όλα τα άλλα endpoints χρειάζονται authentication
                )
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(customAuthEntryPoint)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Logged out successfully\"}");
                            response.setContentType("application/json");
                            response.getWriter().flush();
                        })
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);




        return http.build();

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // 🔥 Επιτρέπουμε το Angular
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Επιτρεπόμενες HTTP μέθοδοι
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        //configuration.setAllowCredentials(true); // 🔥 Ενεργοποιεί τα cookies!
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}


//JWT WITH CSRF

    /*
    ✅ Διόρθωση & Σωστή Ροή
🔹 Backend (Spring Boot)
Δημιουργεί το JWT Token όταν ο χρήστης κάνει login.
Το αποθηκεύει στο Server; ❌ Όχι. Το JWT είναι stateless και δεν αποθηκεύεται στο backend.
Το επιστρέφει ως HttpOnly Cookie στο response.
Δημιουργεί ένα CSRF Token και το στέλνει ως Header και στο response body.
🔹 Frontend (Angular)
Διαβάζει το CSRF Token από το response header και το αποθηκεύει στο sessionStorage.
Το προσθέτει στα headers σε κάθε επικίνδυνο request (POST, PUT, DELETE).
Δεν ασχολείται με το JWT Token. Τα HttpOnly cookies στέλνονται αυτόματα από τον browser με κάθε request.
     */

//ΔΙΚΑ ΜΟΥ

//ΣΥΝΕΧΙΖΩ ΜΕ CUSTOM LOGIN REST API
//ΩΣ AUTHETICATE ΘΑ ΧΡΗΣΙΜΟΠΟΙΗΣΩ ΜΑΛΛΟΝ ΤΟ JWT
//JWT -> ΕΠΙΚΟΙΝΩΝΙΑ BACK ME FRONT
// AUTHENTICATION MANAGER ->ΕΛΕΓΧΟΣ CRED ΧΡΗΣΤΗ ΑΠΟ ΤΗ ΒΑΣΗ
//JWT ΑΝΤΙ ΓΙΑ COOKIES KAI SESIONS
//ΑΝ ΧΡΗΣΙΜΟΠΟΙΩ JWT ΔΕΝ ΧΡΙΑΖΟΜΑΙ CSRF
//ΕΔΩ (CONFIG) ΜΠΑΙΝΟΥΝ ΤΑ BEANS KAI ΣΤΙΣ ΑΛΛΕΣ ΚΛΑΣΕΙΣ ΤΑ AUTOWIRED
//ΕΦΤΙΑΞΑ jWTUTIL ΓΙΑ ΔΗΜΙΟΥΡΓΙΑ JWT, ΤΟ ΕΒΑΛΑ ΣΤΟ LOGIN ΝΑ ΦΤΙΑΧΝΕΤΑΙ ΚΑΙ ΝΑ ΓΥΡΙΖΕΙ ΠΙΣΩ ->ΤΩΡΑ ΠΡΕΠΕΙ ΝΑ ΤΟ ΕΝΕΡΓΟΠΟΙΗΣΩ ΣΕ ΟΛΑ ΤΑ POST, ΝΑ ΒΑΛΩ FILTER ΚΑΙ ΝΑ ΦΤΙΑΞΩ ΤΟ CONFIG.
//ΕΦΤΙΑΞΑ ΤΟ FILTER ΚΑΙ ΤΟ CONFIG. ΠΡΕΠΕΙ ΝΑ ΔΙΟΡΘΩΣΩ ΤΟ UsernamePasswordAuthenticationToken ΣΤΟ FILTER ΟΣΟΝ ΑΦΟΡΑ ΤΟΥΣ ΡΟΛΟΥΣ (ΝΑ ΤΟ ΞΑΝΑΔΩ)
//ΓΕΝΙΚΗ ΑΡΧΗ:
//ΚΑΝΕΙ Ο ΧΡΗΣΤΗΣ LOGIN ΧΩΡΙΣ TOKEN (ΕΔΩ ΘΑ ΠΡΕΠΕΙ ΜΑΛΛΟΝ ΑΠΟ FRONT ΝΑ ΦΕΥΓΟΥΝ ΚΡΥΠΤΟ ΤΑ ΣΤΟΙΧΕΙΑ ΤΟΥ)
//ΕΠΙΣΤΡΕΦΕΤΑΙ ΕΝΑ ΤΟΚΕΝ
//ΓΙΑ ΚΑΘΕ ΝΕΟ REQUSET ΘΕΛΕΙ ΤΟ ΤΟΚΕΝ ΣΤΑ HEADERS ΩΣ AUTHORIZATION ME BEARER KENO ΚΑΙ ΜΕΤΑ ΤΟ ΤΟΚΕΝ ΧΩΡΙΣ "".
//ΤΟ ΦΙΛΤΡΟ ΕΛΕΓΧΕΙ ΑΝ ΤΟ ΤΟΚΕΝ ΕΙΝΑΙ ΕΓΚΥΡΟ ΚΑΙ ΕΓΚΑΙΡΟ (ΝΑ ΔΩ ΓΕΝΙΚΑ ΓΙΑ ΤΟ JWT)
//ΣΤΗ ΣΥΝΕΧΕΙΑ ΕΞΑΓΕΙ ΑΠΟ ΕΚΕΙ ΤΟ USERNAME KAI ΜΕΤΑ ΟΛΑ ΤΑ ΣΤΟΙΧΕΙΑ ΤΟΥ ΧΡΗΣΤΗ ΚΑΙ ΠΡΟΧΩΡΑΕΙ ΣΤΟ AUTHETICATION MANAGER ΟΠΟΥ ΚΑΝΕΙ ΑΝΑΖΗΤΗΣΕΙ
//ΣΤΗ ΒΑΣΗ ΚΑΛΩΝΤΑΣ ΤΟ DAO PROVIDER ΚΑΙ ΑΥΤΟ ΤΗΝ USERDETAILSERVICE. ΟΤΑΝ ΒΡΕΙ ΤΑ ΣΤΟΙΧΕΙΑ ΤΑ ΑΠΟΘΗΚΕΥΕΙ ΣΤΟ SECURITYCONTEX(LOGIKA->ΝΑ ΤΟ ΜΕΛΕΤΗΣΩ ΤΟ ΤΕΛΕΥΤΑΙΟ)



//RESPONCE HEADER VS RESPONCE BODY
/*
✅ 1️⃣ Response Header
Περιέχει μετα-δεδομένα (metadata) που συνοδεύουν το response.
Δεν είναι μέρος του ίδιου του περιεχομένου (body) της απάντησης.
Χρησιμοποιείται κυρίως από τον browser και το frontend για ασφάλεια, caching, authentication κ.λπ.
📌 Παράδειγμα CSRF token σε response header:

http
Αντιγραφή κώδικα
HTTP/1.1 200 OK
X-CSRF-TOKEN: 123abc456def
Content-Type: application/json
Το Angular μπορεί να πάρει το token από εδώ με response.headers.get("X-CSRF-TOKEN").
Ο browser δεν αποθηκεύει αυτόματα τα custom headers (άρα πρέπει να το διαχειριστούμε χειροκίνητα στο Angular).
✅ 2️⃣ Response Body
Περιέχει το ίδιο το περιεχόμενο της απάντησης.
Διαβάζεται συνήθως από το frontend (π.χ., JSON response).
Δεν χρησιμοποιείται από τον browser για security mechanisms.
📌 Παράδειγμα CSRF token σε response body:

json
Αντιγραφή κώδικα
{
  "csrfToken": "123abc456def"
}
Το Angular μπορεί να το διαβάσει από response.body.csrfToken.
 */

//ΤΙ ΣΤΕΛΝΩ ΚΑΙ ΠΩΣ
//ΜΕΤΑ ΤΟ LOGIN ΣΤΕΛΝΩ ΓΙΑ ΟΛΑ ΕΚΤΟΣ GET ΚΑΙ JWT ΚΑΙ CSRF ΣTΑ HEADERS

//ΤΕΛΙΚΟ!!!!!
//ΧΡΗΣΗ ΜΟΝΟ JWT ΤΟ ΟΠΟΙΟ ΣΤΕΛΝΕΤΑΙ ΩΣ HEADER STO RESPONCE
//ΕΙΝΑΙ ΔΟΥΛΕΙΑ ΤΟΥ BACKEND ΝΑ ΤΟ ΑΠΟΘΗΛΕΥΕΙ (LOCAL STORAGE Ή SEESION STORAGE) ΚΑΙ ΤΟ ΧΡΗΣΙΜΟΠΟΙΕΙ ΣΕ ΚΑΘΕ REQUSET ΣΤΟ BODY
//ΔΕΝ ΧΡΕΙΑΖΕΤΑΙ ΚΑΙ CSRF
//TO JWT EINAI STATELEESS ΠΟΥ ΣΗΜΑΙΝΕΙ ΟΤΙ ΔΕΝ ΧΡΗΣΙΜΟΠΟΙΕΙ COOKIES
//ΕΙΝΑΙ ΔΟΥΛΕΙΑ ΤΟΥ FRONTEND ΝΑ ΔΙΑΓΡΑΨΕΙ ΤΟ JWT ΜΕΤΑ ΤΟ LOGOUT