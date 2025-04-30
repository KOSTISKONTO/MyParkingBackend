package myParking_Backend.Backend.Parking.Prices.PolicyPrices;
import jakarta.persistence.*;

@Entity
@Table(name="ByHour")
public class ByHour {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="fromHour")
    private double fromHour;

    @Column(name="toHour")
    private double toHour;

    @Column(name="costofhour")
    private double costofhour;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_policy_id")
    private GeneralPolicy generalPolicy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFromHour() {
        return fromHour;
    }

    public void setFromHour(double fromHour) {
        this.fromHour = fromHour;
    }

    public double getToHour() {
        return toHour;
    }

    public void setToHour(double toHour) {
        this.toHour = toHour;
    }

    public double getCost() {
        return costofhour;
    }

    public void setCost(double costofhour) {
        this.costofhour = costofhour;
    }

    public double getCostofhour() {
        return costofhour;
    }

    public void setCostofhour(double costofhour) {
        this.costofhour = costofhour;
    }


    public GeneralPolicy getGeneralPolicy() {
        return generalPolicy;
    }

    public void setGeneralPolicy(GeneralPolicy generalPolicy) {
        this.generalPolicy = generalPolicy;
    }
}
