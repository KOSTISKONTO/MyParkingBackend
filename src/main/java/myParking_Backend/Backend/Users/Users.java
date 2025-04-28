package myParking_Backend.Backend.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import myParking_Backend.Backend.Bookings.Booking;
import myParking_Backend.Backend.Parking.Parking;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;


@Entity
@Table(name="Users_Table")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Users implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="username", nullable=false, unique = true)
    private String username;


    @Column(name="password", nullable=false, unique = true)
    private String password;

    @JsonProperty("name")
    @Column(name="name", nullable=false)
    private String name;

    @JsonProperty("last")
    @Column(name="lastname", nullable=false)
    private String lastname;

    @Column (name = "email", nullable=false, unique = true)
    private String email;

    @JsonProperty("IsCustomer")
    @Column(name="IsCustomer", nullable=false)
    private Boolean IsCustomer;

    @JsonProperty("IsOwner")
    @Column(name="IsOwner", nullable=false)
    private Boolean IsOwner;



    @JsonProperty("Identity_owner")
    @Column(name="Identity_owner", nullable=false)
    private String Identity;


    //ΠΡΕΠΕΙ  ΝΑ ΒΡΩ ΤΡΟΠΟ ΓΙΑ UNIQUE
    @JsonProperty("Afm_Owner")
    @Column(name="Afm_Owner", nullable=false)
    private String Afm_Owner;


    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Parking> parkings = new HashSet<>();

    @OneToMany(mappedBy= "user_of_booking")
    @JsonIgnore
    private Set<Booking> user_of_booking = new HashSet<>();

    //@JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;







    public Users(Long id, String username, String password, String email, Boolean isCustomer, Boolean isOwner, String afmowner, Set<Parking> parkings) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        IsCustomer = isCustomer;
        IsOwner = isOwner;
        this.Afm_Owner = afmowner;
        this.parkings = parkings;
    }

    public Users() {

    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))  // Προσθήκη του ROLE_ μπροστά από το όνομα του ρόλου
                .collect(Collectors.toSet());
    }
    //@JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }





    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getCustomer() {
        return IsCustomer;
    }

    public void setCustomer(Boolean customer) {
        IsCustomer = customer;
    }

    public Boolean getOwner() {
        return IsOwner;
    }

    public void setOwner(Boolean owner) {
        IsOwner = owner;
    }

    public String getAfm_Owner() {
        return Afm_Owner;
    }

    public void setAfm_Owner(String afm_Owner) {
        Afm_Owner = afm_Owner;
    }

    public Set<Parking> getParkings() {
        return parkings;
    }

    public void setParkings(Set<Parking> parkings) {
        this.parkings = parkings;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<Booking> getUser_of_booking() {
        return user_of_booking;
    }

    public void setUser_of_booking(Set<Booking> user_of_booking) {
        this.user_of_booking = user_of_booking;
    }

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}


