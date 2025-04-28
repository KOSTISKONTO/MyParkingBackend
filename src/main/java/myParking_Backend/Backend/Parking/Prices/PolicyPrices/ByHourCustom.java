package myParking_Backend.Backend.Parking.Prices.PolicyPrices;
import jakarta.persistence.*;


@Entity
@Table(name="ByHourCustom")
public class ByHourCustom {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="fromHour")
    private double fromHour;

    @Column(name="toHour")
    private double toHour;

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
