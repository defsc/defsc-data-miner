package pl.edu.agh.defsc.entity.localizations.impl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pl.edu.agh.defsc.entity.GeoPoint;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;

@Document
public class WundergroundSensor implements LocalizationOfMeasurements {

    @Id
    private String id;

    @Field("location")
    private GeoPoint localization;

    @Field("elevation")
    private Integer elevation;

    @Field("rtfreq")
    private Double rtfreq;

    @Field("neighborhood")
    private String neighborhood;

    @Field("softwareType")
    private String softwareType;

    @Field("name")
    private String name;

    @Field("country")
    private String country;

    @Field("type")
    private String type;

    @Field("adm1")
    private String adm1;

    @Field("adm2")
    private String adm2;

    public void setId(String id) {
        this.id = id;
    }

    public void setLocalization(GeoPoint localization) {
        this.localization = localization;
    }

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    public Double getRtfreq() {
        return rtfreq;
    }

    public void setRtfreq(Double rtfreq) {
        this.rtfreq = rtfreq;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getSoftwareType() {
        return softwareType;
    }

    public void setSoftwareType(String softwareType) {
        this.softwareType = softwareType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdm1() {
        return adm1;
    }

    public void setAdm1(String adm1) {
        this.adm1 = adm1;
    }

    public String getAdm2() {
        return adm2;
    }

    public void setAdm2(String adm2) {
        this.adm2 = adm2;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public GeoPoint getLocalization() {
        return localization;
    }
}