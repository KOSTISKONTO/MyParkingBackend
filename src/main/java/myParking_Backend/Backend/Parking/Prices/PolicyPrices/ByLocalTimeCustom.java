package myParking_Backend.Backend.Parking.Prices.PolicyPrices;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name="ByLocalTimeCustom")
public class ByLocalTimeCustom {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="fromhour")
    private LocalTime fromhour;

    @Column(name="tohour")
    private LocalTime tohour;

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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public GeneralPolicy getGeneralPolicy() {
        return generalPolicy;
    }

    public void setGeneralPolicy(GeneralPolicy generalPolicy) {
        this.generalPolicy = generalPolicy;
    }
}
