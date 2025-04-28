package myParking_Backend.Backend.Parking.Prices.PolicyPrices;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import myParking_Backend.Backend.Parking.Parking;
import myParking_Backend.Backend.Parking.Prices.Enum.DayType;
import myParking_Backend.Backend.Parking.Prices.Enum.Policy;

@Entity
@Table(name="GeneralPolicy")
public class GeneralPolicy {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayType dayType; // DAILY, WEEKEND, HOLIDAY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;

    @JsonProperty("FlatCost")
    @OneToOne(mappedBy = "generalPolicy", cascade = CascadeType.ALL)
    private FlatCost FlatCost;

    @JsonProperty("CostByHour")
    @OneToOne(mappedBy = "generalPolicy",cascade = CascadeType.ALL)
    private CostByHour costByHour;

    @JsonProperty("ByHour")
    @OneToMany(mappedBy = "generalPolicy", cascade = CascadeType.ALL)
    private Set <ByHour> ByHour;

    @JsonProperty("ByHourCustom")
    @OneToMany(mappedBy = "generalPolicy", cascade = CascadeType.ALL)
    private Set<ByHourCustom> ByHourCustom;

    @JsonProperty("ByLocalTime")
    @OneToMany(mappedBy = "generalPolicy", cascade = CascadeType.ALL)
    private Set<ByLocalTime> ByLocalTime;

    @JsonProperty("ByLocalTimeCustom")
    @OneToMany(mappedBy = "generalPolicy", cascade = CascadeType.ALL)
    private Set<ByLocalTimeCustom> ByLocalTimeCustom;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    public FlatCost getFlatCost() {
        return FlatCost;
    }

    public void setFlatCost(FlatCost flatCost) {
        this.FlatCost = flatCost;
    }

    public CostByHour getCostByHour() {
        return costByHour;
    }

    public void setCostByHour(CostByHour costByHour) {
        this.costByHour = costByHour;
    }

    public Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByHour> getByHour() {
        return ByHour;
    }

    public void setByHour(Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByHour> byHour) {
        ByHour = byHour;
    }

    public Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByHourCustom> getByHourCustom() {
        return ByHourCustom;
    }

    public void setByHourCustom(Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByHourCustom> byHourCustom) {
        ByHourCustom = byHourCustom;
    }

    public Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByLocalTime> getByLocalTime() {
        return ByLocalTime;
    }

    public void setByLocalTime(Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByLocalTime> byLocalTime) {
        ByLocalTime = byLocalTime;
    }

    public Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByLocalTimeCustom> getByLocalTimeCustom() {
        return ByLocalTimeCustom;
    }

    public void setByLocalTimeCustom(Set<myParking_Backend.Backend.Parking.Prices.PolicyPrices.ByLocalTimeCustom> byLocalTimeCustom) {
        ByLocalTimeCustom = byLocalTimeCustom;
    }
}
