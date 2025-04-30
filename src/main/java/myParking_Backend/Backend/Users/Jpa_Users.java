package myParking_Backend.Backend.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface Jpa_Users extends JpaRepository<Users, Long> {


    //Optional<Users> findById(Long id);

    Optional<Users> findByUsername(String username);
    Users getUserById(Long id);
    boolean existsUsersByUsername(String username);
    boolean existsUsersByEmail(String email);
    boolean existsUsersById(Long Id);

    @Query(value = "SELECT COUNT(*) > 0 FROM Users_Table WHERE Afm_Owner = :afm", nativeQuery = true)
    boolean existsByAfmOwner(@Param("afm") String afm);

    Users save(Users user);

    @Query("SELECT user FROM Users user WHERE user.Afm_Owner = :Afm_Owner")
    Users getUsersByAfmOwner(String Afm_Owner);

    @Query(value = "SELECT * FROM USERS_TABLE WHERE ISCUSTOMER=TRUE", nativeQuery = true)
    List <Users> getUsersCustomer();

    @Query(value = "SELECT * FROM USERS_TABLE WHERE ISOWNER=TRUE", nativeQuery = true)
    List <Users> getOwners();

}
