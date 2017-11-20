package pl.edu.agh.defsc.deserializer;

import com.google.gson.*;
import pl.edu.agh.defsc.entity.Address;
import pl.edu.agh.defsc.entity.GeoPoint;
import pl.edu.agh.defsc.entity.localizations.AirlyLocalizationOfMeasurements;

import java.lang.reflect.Type;

public class AirlyLocalizationOfMeasurementsDeserializer implements JsonDeserializer<AirlyLocalizationOfMeasurements> {
    @Override
    public AirlyLocalizationOfMeasurements deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();

        AirlyLocalizationOfMeasurements localization = null;
        JsonObject jsonAddress = json.get("address").getAsJsonObject();
        Integer id = json.get("id").getAsInt();
        String country = jsonAddress.get("country").getAsString();
        String strAddress = json.get("address").getAsJsonObject().get("locality").getAsString();
        String streetName = "";
        String streetNumber = null;

        if (jsonAddress.get("route") != null)
            streetName = jsonAddress.get("route").getAsString();

        if (jsonAddress.get("streetNumber") != null)
            streetNumber = jsonAddress.get("streetNumber").getAsString();

        Address address = new Address(
                country,
                strAddress,
                streetName,
                streetNumber
        );
        GeoPoint geoPoint = new GeoPoint(
                json.get("location").getAsJsonObject().get("longitude").getAsDouble(),
                json.get("location").getAsJsonObject().get("latitude").getAsDouble()
        );
        String vendor = json.get("vendor").getAsString();
        String name = json.get("name").getAsString();
        ;

        localization = new AirlyLocalizationOfMeasurements(id, geoPoint);
        localization.setAddress(address);
        localization.setVendor(vendor);
        localization.setName(name);



        return localization;
    }
}