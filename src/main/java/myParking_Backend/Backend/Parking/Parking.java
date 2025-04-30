package myParking_Backend.Backend.Parking;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import myParking_Backend.Backend.Parking.Prices.PolicyPrices.GeneralPolicy;
import myParking_Backend.Backend.Users.Users;
import java.util.*;

@Entity
@Table(name="Parking")
@JsonInclude(JsonInclude.Include.NON_NULL) //ΧΡΗΣΜΟΠΟΙΕΤΑΙ ΩΣΤΕ ΤΑ NULL ΣΤΟΙΧΕΙΑ ΝΑ ΜΗΝ ΕΠΙΣΤΡΕΦΟΝΤΑΙ
public class Parking {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name_of_parking", nullable = false)
    private String name_of_parking;

    @JsonProperty("City")
    @Column(name = "City", nullable = false)
    private String City;

    @JsonProperty("address")
    @Column(name = "address", nullable = false)
    private String address;

    @JsonProperty("location")
    @Column(name="location", nullable=false)
    private String location;

    @JsonProperty("TK")
    @Column(name = "TK", nullable = false)
    private String TK;


    @JsonProperty("afm")
    @Column(name = "afm", nullable = false, unique = true)
    private String afm;

    @JsonProperty("isactive")
    @Column(name="isactive", nullable=false)//
    private Boolean isactive=false;

    @JsonProperty("status")
    @Column(name="status", nullable=false)//TODO NULLABLE=FALSE
    private String status;

    @JsonProperty("total_spaces")
    @Column(name="total_spaces", nullable=false)
    private int total_spaces;

    @JsonProperty("available_spaces")
    @Column(name="available_spaces")
    private int available_spaces;

    @JsonFormat(pattern = "HH:mm")
    @Column(name="startworking")
    private LocalTime startworking;


    @JsonFormat(pattern = "HH:mm")
    @Column(name="endworking")
    private LocalTime endworking;

    @JsonProperty("Is24")
    @Column(name="Is24", nullable=false)
    private Boolean Is24;

    @JsonProperty("Accept24")
    @Column(name="Accept24")
    private Boolean Accept24;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="parking")
    Set<GeneralPolicy> generalPolicyList;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "owner_parking", // Το όνομα του πίνακα που κρατά τις σχέσεις
            joinColumns = @JoinColumn(name = "parking_id"),  // Ξένο κλειδί για το Parking
            inverseJoinColumns = @JoinColumn(name = "user_id") // Ξένο κλειδί για τον User
    )
    private Set<Users> users = new HashSet<>();


    @ElementCollection
    @Column(name = "file_path")
    private List<String> documentPaths = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName_of_parking() {
        return name_of_parking;
    }

    public void setName_of_parking(String name_of_parking) {
        this.name_of_parking = name_of_parking;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTK() {
        return TK;
    }

    public void setTK(String TK) {
        this.TK = TK;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public int getTotal_spaces() {
        return total_spaces;
    }

    public void setTotal_spaces(int total_spaces) {
        this.total_spaces = total_spaces;
    }

    public int getAvailable_spaces() {
        return available_spaces;
    }

    public void setAvailable_spaces(int available_spaces) {
        this.available_spaces = available_spaces;
    }

    public LocalTime getStartworking() {
        return startworking;
    }

    public void setStartworking(LocalTime startworking) {
        this.startworking = startworking;
    }

    public LocalTime getEndworking() {
        return endworking;
    }

    public void setEndworking(LocalTime endworking) {
        this.endworking = endworking;
    }

    public Boolean getIs24() {
        return Is24;
    }

    public void setIs24(Boolean is24) {
        Is24 = is24;
    }

    public Set<GeneralPolicy> getGeneralPolicyList() {
        return generalPolicyList;
    }

    public void setGeneralPolicyList(Set<GeneralPolicy> generalPolicyList) {
        this.generalPolicyList = generalPolicyList;
    }

    public Boolean getAccept24() {
        return Accept24;
    }

    public void setAccept24(Boolean accept24) {
        Accept24 = accept24;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getDocumentPaths() {
        return documentPaths;
    }

    public void setDocumentPaths(List<String> documentPaths) {
        this.documentPaths = documentPaths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}



