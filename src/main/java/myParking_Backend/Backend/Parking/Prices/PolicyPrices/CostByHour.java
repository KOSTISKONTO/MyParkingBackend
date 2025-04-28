package myParking_Backend.Backend.Parking.Prices.PolicyPrices;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name="CostByHour")
public class CostByHour {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_policy_id")
    private GeneralPolicy generalPolicy;


    @JsonProperty("cost")
    @Column(name="CostofHour")
    private double costOfHour;

    public GeneralPolicy getGeneralPolicy() {
        return generalPolicy;
    }

    public void setGeneralPolicy(GeneralPolicy generalPolicy) {
        this.generalPolicy = generalPolicy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCostOfHour() {
        return costOfHour;
    }

    public void setCostOfHour(double costOfHour) {
        this.costOfHour = costOfHour;
    }
}
