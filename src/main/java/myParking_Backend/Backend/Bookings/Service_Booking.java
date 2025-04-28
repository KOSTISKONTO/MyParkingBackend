package myParking_Backend.Backend.Bookings;
import java.time.LocalDate;
import java.time.LocalTime;
import  java.util.*;
import myParking_Backend.Backend.Parking.Jpa_Parking;
import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Service_Booking{


    private Jpa_Bookings jpa_Bookings;
    private Jpa_Parking jpa_Parking;


    @Autowired
    public Service_Booking(Jpa_Bookings jpa_Bookings, Jpa_Parking jpa_parking) {
        this.jpa_Bookings = jpa_Bookings;
        this.jpa_Parking = jpa_parking;
    }

    public  void newBooking(Booking booking){
        this.jpa_Bookings.save(booking);
    }

    public List<Booking> findByUsers_of_booking (Users user){
        return this.jpa_Bookings.findBookingsByUser(user);
    }

    public List<Booking> getBookingsByDateandTime(LocalDate booking_date, LocalTime time_start, LocalTime endTime){
        return this.jpa_Bookings.getBookingsByDateAndTime(booking_date, time_start, endTime);
    }

    public List<ResponceCities> getpopularCities(){
        return this.jpa_Bookings.getTopBookedCities();
    }


}
