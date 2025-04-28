package myParking_Backend.Backend.Parking;

import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Users.Users;
import org.springframework.stereotype.Service;

@Service
public class Service_Parking {
    private Jpa_Parking jpa_Parking;

    @PersistenceContext
    private EntityManager entityManager;

    public Service_Parking(Jpa_Parking jpa_Parking) {
        this.jpa_Parking = jpa_Parking;
    }

    public void savewithmanyusers(Parking parking, Set<Users> users){
       for (Users user:users){
           parking.getUsers().add(user);
       }
       this.jpa_Parking.save(parking);
    }

    public void savewithoneuser(Parking parking, Users user){
        parking.getUsers().add(user);
        this.jpa_Parking.save(parking);

    }

    public void save(Parking parking){
        this.jpa_Parking.save(parking);
    }

    public Set<Parking> getParkingByUsers(Users user){
        return this.jpa_Parking.getParkingByUsers(user);
    }


    public Parking findParkingByName_of_parking(String name){
        return this.jpa_Parking.getParkingByNameOfParking(name);
    }

    public Parking getParkingById(Long Id){
        return this.jpa_Parking.getReferenceById(Id);
    }

    public List<ResponceCities> getCities(){
        return this.jpa_Parking.getCities();
    }

    public List<Parking> getParkingsByCity(String City){
        return this.jpa_Parking.getParkingByCity(City);
    }

    public List<Parking> getParkings(){
        return this.jpa_Parking.getAllParkings();
    }


}
