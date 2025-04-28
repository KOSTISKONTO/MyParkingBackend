package myParking_Backend.Backend.Parking.Prices.PolicyPrices;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name="ByLocalTime")
public class ByLocalTime {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="fromhour")
    private LocalTime fromhour;

    @Column(name="tohour")
    private LocalTime tohour;

    @JsonProperty("cost")
    @Column(name="cost")
    private double cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_policy_id")
    private GeneralPolicy generalPolicy;

















    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getFromhour() {
        return fromhour;
    }

    public void setFromhour(LocalTime fromhour) {
        this.fromhour = fromhour;
    }

    public LocalTime getTohour() {
        return tohour;
    }

    public void setTohour(LocalTime tohour) {
        this.tohour = tohour;
    }

    public double getCostofTime() {
        return cost;
    }

    public void setCostofTime(double costofTime) {
        this.cost = costofTime;
    }

    public GeneralPolicy getGeneralPolicy() {
        return generalPolicy;
    }

    public void setGeneralPolicy(GeneralPolicy generalPolicy) {
        this.generalPolicy = generalPolicy;
    }
}
