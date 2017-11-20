package pl.edu.agh.defsc.entity.localizations;

import pl.edu.agh.defsc.entity.Address;
import pl.edu.agh.defsc.entity.GeoPoint;

public class AirlyLocalizationOfMeasurements extends LocalizationOfMeasurements<Integer> {
    private Address address;
    private String name;
    // TO-DO replace with enum
    private String vendor;

    public AirlyLocalizationOfMeasurements(Integer id, GeoPoint geoPoint) {
        super(id, geoPoint);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return super.getGeoPoint();
    }

    public void setLocation(GeoPoint location) {
        super.setGeoPoint(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return "AirlyLocalizationOfMeasurements{" +
                "address=" + address +
                ", location=" + super.getGeoPoint() +
                ", name='" + name + '\'' +
                ", vendor='" + vendor + '\'' +
                '}';
    }
}
