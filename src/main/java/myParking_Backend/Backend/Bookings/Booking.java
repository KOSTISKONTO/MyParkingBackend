package myParking_Backend.Backend.Bookings;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import myParking_Backend.Backend.Parking.Parking;
import myParking_Backend.Backend.Users.Users;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="booking")
@JsonInclude(JsonInclude.Include.NON_NULL) //ΧΡΗΣΜΟΠΟΙΕΤΑΙ ΩΣΤΕ ΤΑ NULL ΣΤΟΙΧΕΙΑ ΝΑ ΜΗΝ ΕΠΙΣΤΡΕΦΟΝΤΑΙ
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="booking_make_date", nullable= false)
    private LocalDate booking_making_date;

    @Column(name="booking_date", nullable=false)
    private LocalDate booking_date;

    //@JsonFormat(pattern = "HH:mm")
    @Column(name = "time_date", nullable=false)
    private LocalTime time_date;

    @Column (name="hours_of_booking", nullable=false)
    private int hours_of_booking;

    @Column (name="cost", nullable=false)
    private double cost;

    @JsonProperty("nameguestguest")
    @Column(name="nameguestguest")
    private String nameguest="nameguest";

    @JsonProperty("lastnameguest")
    @Column(name="lastnameguest")
    private String lastnameguest;

    @JsonProperty("emailguset")
    @Column(name="emailguset")
    private String emailguest;


    @JsonProperty("numberofvehicle")
    @Column(name="numberofvehicle", nullable=false)
    private String numberofvehicle;

    @JsonProperty("brandvehicle")
    @Column(name="brandvehicle", nullable=false)
    private String brandvehicle;


    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="user_of_booking", nullable = true)
    private Users user_of_booking;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "booking_parking")
    private Parking booking_parking;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getHours_of_time() {
        return hours_of_booking;
    }

    public void setHours_of_time(int hours_of_time) {
        this.hours_of_booking = hours_of_time;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getHours_of_booking() {
        return hours_of_booking;
    }

    public void setHours_of_booking(int hours_of_booking) {
        this.hours_of_booking = hours_of_booking;
    }

    public Users getUser_of_booking() {
        return user_of_booking;
    }

    public void setUser_of_booking(Users user_of_booking) {
        this.user_of_booking = user_of_booking;
    }

    public Parking getBooking_parking() {
        return booking_parking;
    }

    public void setBooking_parking(Parking booking_parking) {
        this.booking_parking = booking_parking;
    }

    public String getNameguest() {
        return nameguest;
    }

    public void setNameguest(String name) {
        this.nameguest = name;
    }

    public String getLastnameguest() {
        return lastnameguest;
    }

    public void setLastnameguest(String lastnameguest) {
        this.lastnameguest = lastnameguest;
    }

    public String getEmailguest() {
        return emailguest;
    }

    public void setEmailguest(String emailguest) {
        this.emailguest = emailguest;
    }

    public String getNumberofvehicle() {
        return numberofvehicle;
    }

    public void setNumberofvehicle(String numberofvehicle) {
        this.numberofvehicle = numberofvehicle;
    }

    public String getBrandvehicle() {
        return brandvehicle;
    }

    public void setBrandvehicle(String brandvehicle) {
        this.brandvehicle = brandvehicle;
    }
}
