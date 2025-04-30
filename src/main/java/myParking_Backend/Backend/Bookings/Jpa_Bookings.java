package myParking_Backend.Backend.Bookings;

import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import  java.util.*;


public interface Jpa_Bookings extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.user_of_booking = :user") //ΔΕΝ ΤΟ ΕΒΡΙΣΚΕ ΑΠΟ ΜΟΝΟ ΤΟΥ - ΧΕΙΡΟΚΙΝΗΤΗ ΛΥΣΗ
    List<Booking> findBookingsByUser(@Param("user") Users user);

    @Query(value = """
    SELECT * FROM booking 
    WHERE booking_date = :date 
      AND (
        time_date < :endTime
        AND (time_date + (hours_of_booking * INTERVAL '1 hour')) > :startTime
      )
""", nativeQuery = true)
    List<Booking> getBookingsByDateAndTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );



    @Query(value = """
    SELECT p.city AS namecity, p.address, p.tk, COUNT(*) AS total_bookings
    FROM booking b
    JOIN parking p ON b.booking_parking = p.id
    GROUP BY p.city, p.address, p.tk
    ORDER BY total_bookings DESC
    LIMIT 5
""", nativeQuery = true)
    List<ResponceCities> getTopBookedCities();




}
