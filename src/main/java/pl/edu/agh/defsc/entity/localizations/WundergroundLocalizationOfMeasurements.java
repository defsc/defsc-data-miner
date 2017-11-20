package pl.edu.agh.defsc.entity.localizations;

import pl.edu.agh.defsc.entity.GeoPoint;

public class WundergroundLocalizationOfMeasurements extends LocalizationOfMeasurements<String> {
    public WundergroundLocalizationOfMeasurements(String id, GeoPoint geoPoint) {
        super(id, geoPoint);
    }
}
