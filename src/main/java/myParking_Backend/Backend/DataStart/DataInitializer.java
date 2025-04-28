package myParking_Backend.Backend.DataStart;
import myParking_Backend.Backend.Users.Service_Users;
import myParking_Backend.Backend.Users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DataInitializer {

    private final Service_Users service_users;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(Service_Users service_users, PasswordEncoder passwordEncoder) {
        this.service_users = service_users;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            try {
                service_users.loadUserByUsername("admin");
                System.out.println("Admin user already exists.");
            } catch (UsernameNotFoundException e) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                Set<String> roles = new HashSet<>();
                roles.add("ROLE_ADMIN");
                admin.setRoles(roles);
                admin.setLastname("konto");
                admin.setName("kostis");
                admin.setEmail("kkontogiannis1991@gmail.com");
                admin.setIdentity("NONE");
                admin.setAfm_Owner("NONE");
                admin.setCustomer(false);
                admin.setOwner(false);
                service_users.saveuser(admin, roles);
                System.out.println("Admin user created!");
            }
        };
    }
}

