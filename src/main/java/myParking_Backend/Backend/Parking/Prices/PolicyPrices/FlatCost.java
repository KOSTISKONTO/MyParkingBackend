package myParking_Backend.Backend.Parking.Prices.PolicyPrices;
import jakarta.persistence.*;

@Entity
@Table(name="FlatCost")
public class FlatCost {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="flatcost")
    private double flatcost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_policy_id")
    private GeneralPolicy generalPolicy;


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

    public double getFlatcost() {
        return flatcost;
    }

    public void setFlatcost(double flatcost) {
        this.flatcost = flatcost;
    }
}
