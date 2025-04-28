package myParking_Backend.Backend.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class Service_Users implements UserDetailsService {


    Jpa_Users jpa_Users;
    PasswordEncoder passwordEncoder;


    @Autowired
    public Service_Users(Jpa_Users repo, PasswordEncoder passwordEncoder) {
        this.jpa_Users = repo;
        this.passwordEncoder = passwordEncoder;

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = jpa_Users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Λάθος Διαπιστευτήρια! Όνομα"));

        System.out.println("user " + user.getRoles());
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList()); // Χρησιμοποιούμε collect για σωστή συλλογή

        return user;
    }


    public Users getUserById(Long Id){
        return this.jpa_Users.getUserById(Id);
    }

    public Boolean existsUsersById(Long Id){
        System.out.println(7);
        return this.jpa_Users.existsUsersById(Id);
    }

    public Boolean existUserByAfm_Owner(String Afm_Owner){
        return this.jpa_Users.existsByAfmOwner(Afm_Owner);
    }

    public Users getUserByAfm_Owner(String Afm_Owner){
        return this.jpa_Users.getUsersByAfmOwner(Afm_Owner);
    }


    public Users saveuser(Users user, Set<String> roles){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        jpa_Users.save(user);
        return user;
    }

    public Boolean existsByUserName(Users user){
        return jpa_Users.existsUsersByUsername(user.getUsername());
    }

    public Boolean existsByEmail(Users user){
        return jpa_Users.existsUsersByEmail(user.getEmail());
    }

    public void updateUser (Users user){
            jpa_Users.save(user);
    }

    public void updateUserPassword (Users user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        jpa_Users.save(user);
    }

    public List<Users> getCustomers(){
        return this.jpa_Users.getUsersCustomer();
    }

    public List<Users> getOwners(){
        return this.jpa_Users.getOwners();
    }

}
