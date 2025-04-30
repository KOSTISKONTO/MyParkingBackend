package myParking_Backend.Backend.Users.ResponceEntities;

import java.util.*;
public class ResponseOwners {

    private Long Id;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String AfmOwner;
    private String IdentityOwner;
    private List<String> NamesOfParking;
    private List<Long> IdParkings;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAfmOwner() {
        return AfmOwner;
    }

    public void setAfmOwner(String afmOwner) {
        AfmOwner = afmOwner;
    }

    public String getIdentityOwner() {
        return IdentityOwner;
    }

    public void setIdentityOwner(String identityOwner) {
        IdentityOwner = identityOwner;
    }

    public List<String> getNamesOfParking() {
        return NamesOfParking;
    }

    public void setNamesOfParking(List<String> namesOfParking) {
        NamesOfParking = namesOfParking;
    }

    public List<Long> getIdParkings() {
        return IdParkings;
    }

    public void setIdParkings(List<Long> idParkings) {
        IdParkings = idParkings;
    }
}
