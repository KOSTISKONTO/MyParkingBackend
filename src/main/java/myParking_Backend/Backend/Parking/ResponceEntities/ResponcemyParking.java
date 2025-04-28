package myParking_Backend.Backend.Parking.ResponceEntities;

import myParking_Backend.Backend.Parking.Prices.Enum.DayType;
import myParking_Backend.Backend.Parking.Prices.Enum.Policy;

import java.util.*;


public class ResponcemyParking {
    private Long Id;
    private String nameofparking;
    private String address;
    private String City;
    private String TK;
    private String afm;
    private String status;
    private DayType daily;
    private DayType weekend;
    private Policy policy;;

    private List<Map<String, Object>> users;

    private List<Map<String, String>> documentPaths;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNameofparking() {
        return nameofparking;
    }

    public void setNameofparking(String nameofparking) {
        this.nameofparking = nameofparking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
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

    public List<Map<String, Object>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<String, Object>> users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, String>> getDocumentPaths() {
        return documentPaths;
    }

    public void setDocumentPaths(List<Map<String, String>> documentPaths) {
        this.documentPaths = documentPaths;
    }


    public DayType getDaily() {
        return daily;
    }

    public void setDaily(DayType daily) {
        this.daily = daily;
    }

    public DayType getWeekend() {
        return weekend;
    }

    public void setWeekend(DayType weekend) {
        this.weekend = weekend;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}

