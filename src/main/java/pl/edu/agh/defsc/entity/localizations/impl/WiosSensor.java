package pl.edu.agh.defsc.entity.localizations.impl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pl.edu.agh.defsc.entity.Address;
import pl.edu.agh.defsc.entity.GeoPoint;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;

// Conditions required for reusing information
// TO-DO https://danepubliczne.gov.pl/dataset/powietrze-api
@Document(collection = "${wios.air.pollution.sensors.collection.name}")
public class WiosSensor implements LocalizationOfMeasurements {
    @Id
    private String id;

    @Field("location")
    private GeoPoint localization;

    @Field("address")
    private Address address;

    @Field("stationName")
    private String name;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public GeoPoint getLocalization() {
        return localization;
    }

    public void setLocalization(GeoPoint localization) {
        this.localization = localization;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
