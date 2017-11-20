package pl.edu.agh.defsc.entity;

public class GeoPoint {
    private final double lon;
    private final double lat;

    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
