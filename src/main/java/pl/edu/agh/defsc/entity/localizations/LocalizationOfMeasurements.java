package pl.edu.agh.defsc.entity.localizations;

import pl.edu.agh.defsc.entity.GeoPoint;

public interface LocalizationOfMeasurements {
    Object getId();

    GeoPoint getLocalization();
}
