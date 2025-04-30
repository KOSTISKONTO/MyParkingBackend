package myParking_Backend.Backend.Bookings.ResponceEntities;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

//@JsonInclude(JsonInclude.Include.NON_NULL) //ΧΡΗΣΜΟΠΟΙΕΤΑΙ ΩΣΤΕ ΤΑ NULL ΣΤΟΙΧΕΙΑ ΝΑ ΜΗΝ ΕΠΙΣΤΡΕΦΟΝΤΑΙ
public class ResponcemyBookings {


    private Long Id;

    private LocalDate booking_making_date;

    private LocalDate booking_date;

    private LocalTime time_date;

    private int hours_of_booking;

    private double cost;

    private Map<String, Object> parking;


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public LocalDate getBooking_making_date() {
        return booking_making_date;
    }

    public void setBooking_making_date(LocalDate booking_making_date) {
        this.booking_making_date = booking_making_date;
    }


    public LocalDate getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(LocalDate booking_date) {
        this.booking_date = booking_date;
    }

    public LocalTime getTime_date() {
        return time_date;
    }

    public void setTime_date(LocalTime time_date) {
        this.time_date = time_date;
    }

    public int getHours_of_booking() {
        return hours_of_booking;
    }

    public void setHours_of_booking(int hours_of_booking) {
        this.hours_of_booking = hours_of_booking;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Map<String, Object> getParking() {
        return parking;
    }

    public void setParking(Map<String, Object> parking) {
        this.parking = parking;
    }
}
