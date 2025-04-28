package myParking_Backend.Backend.Parking.ResponceEntities;

public class ResponceCities {
    private String namecity;
    private String address;
    private String TK;
    private long totalBookings;

    public ResponceCities(String namecity, String address, String TK, long totalBookings) {
        this.namecity = namecity;
        this.address = address;
        this.TK = TK;
        this.totalBookings = totalBookings;
    }

    public ResponceCities(String namecity, String address, String TK) {
        this.namecity = namecity;
        this.address = address;
        this.TK = TK;
    }

    public String getNamecity() {
        return namecity;
    }

    public void setNamecity(String namecity) {
        this.namecity = namecity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTK() {
        return TK;
    }

    public void setTK(String TK) {
        this.TK = TK;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }
}
