package myParking_Backend.Backend.Parking;

import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface Jpa_Parking extends JpaRepository<Parking, Long> {


    Parking save(Parking parking);

    Set<Parking> getParkingByUsers(Users user);
    Parking getParkingById(Long Id);

    @Query(value = "SELECT * FROM Parking", nativeQuery = true)
    Set<Parking> getParkings();


    @Query("SELECT p FROM Parking p WHERE p.name_of_parking = :name_of_parking")
    Parking getParkingByNameOfParking(String name_of_parking);




    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Parking p WHERE p.name_of_parking = :name")
    Boolean existsByName(@Param("name") String name);

    @Query(value = """
    SELECT City, address, TK FROM Parking WHERE isactive = true
    """, nativeQuery = true)
    List<ResponceCities> getCities();


    @Query(value = "SELECT * FROM Parking WHERE city = :city", nativeQuery = true)
    List<Parking> getParkingByCity(@Param("city") String city);

    @Query(value = "SELECT * FROM Parking", nativeQuery = true)
    List<Parking> getAllParkings();





}