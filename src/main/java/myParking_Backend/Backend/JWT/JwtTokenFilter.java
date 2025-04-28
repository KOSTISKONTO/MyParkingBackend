package myParking_Backend.Backend.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myParking_Backend.Backend.Users.Jpa_Users;
import myParking_Backend.Backend.Users.Users;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenUtil;
    private final Jpa_Users userRepo;

    public JwtTokenFilter(JwtUtil jwtTokenUtil,
                          Jpa_Users userRepo) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {



        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header)) {
            chain.doFilter(request, response);
            return;
        }
        if (!header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }


/*
        ///////////////////////////
        // Ανακτάς το JWT από το cookie
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        else{
            chain.doFilter(request, response);
            return;
        }

        if (!jwtTokenUtil.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }
/////////
*/

        // Get user identity and set it on the spring security context
        Users userDetails =  userRepo
                .findByUsername(jwtTokenUtil.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //ΕΔΩ ΕΛΕΓΧΕΤΑΙ ΤΟ AUTHETICATION (USERNAME KAI PASSWORD KAI ROLES) ΤΟΥ ΧΡΗΣΤΗ->ΑΠΟ ΤΟ ΤΟΚΕΝ ΒΓΑΖΟΥΜΕ ΤΟ USERNAME KAI ΑΠΟ ΚΕΙ ΜΕΤΑ ΑΝΑΖΗΤΑΜΕ ΤΟ ΧΡΗΣΤΗ
        //ΣΤΗ ΒΑΣΗ ΚΑΙ ΠΑΙΡΝΟΥΜΕ ΤΑ ΥΠΟΛΟΙΠΑ
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
                /*
                 userDetails == null ?
                        List.of() :
                        List.of(new SimpleGrantedAuthority("ROLE_OWNER"), new SimpleGrantedAuthority("ROLE_CUSTOMER") ,new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                 */

        );

        //NA TO MELETHSW
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        //NA TO MELETHSW
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private boolean isEmpty(String header) {
        //todo
        return header == null || header.trim().isEmpty();
    }

}


/*
{
    "username":"kostis",
    "password":"1234"
}
 */