package pl.edu.agh.defsc.entity.localizations.impl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pl.edu.agh.defsc.entity.Address;
import pl.edu.agh.defsc.entity.GeoPoint;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;

@Document(collection = "${airly.air.pollution.sensors.collection.name}")
public class AirlySensor implements LocalizationOfMeasurements {
    @Id
    private String id;

    @Field("address")
    private Address address;

    @Field("name")
    private String name;

    @Field("vendor")
    private String vendor;

    @Field("location")
    private GeoPoint localization;

    public AirlySensor(String id, GeoPoint localization) {
        this.id = id;
        this.localization = localization;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public GeoPoint getLocalization() {
        return localization;
    }

    public void setLocation(GeoPoint location) {
        this.localization = localization;
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
        return "AirlySensor{" +
                "address=" + address +
                ", location=" + localization +
                ", name='" + name + '\'' +
                ", vendor='" + vendor + '\'' +
                '}';
    }
}
