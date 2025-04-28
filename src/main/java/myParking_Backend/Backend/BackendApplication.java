package myParking_Backend.Backend;

import myParking_Backend.Backend.Users.Jpa_Users;
import myParking_Backend.Backend.Users.Service_Users;
import myParking_Backend.Backend.Users.Users;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@SpringBootApplication
public class BackendApplication {







	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);


	}

}
