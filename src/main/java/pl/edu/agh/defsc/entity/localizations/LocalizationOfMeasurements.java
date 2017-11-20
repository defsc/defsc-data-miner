package pl.edu.agh.defsc.entity.localizations;

import pl.edu.agh.defsc.entity.GeoPoint;

public class LocalizationOfMeasurements<T> {
    private T id;
    private GeoPoint geoPoint;

    public LocalizationOfMeasurements(T id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Override
    public String toString() {
        return "LocalizationOfMeasurements{" +
                "id=" + id +
                ", geoPoint=" + geoPoint +
                '}';
    }
}
