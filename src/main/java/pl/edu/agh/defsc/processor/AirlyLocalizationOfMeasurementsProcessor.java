package pl.edu.agh.defsc.processor;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.entity.localizations.AirlyLocalizationOfMeasurements;

import java.util.LinkedList;
import java.util.List;

@Component
public class AirlyLocalizationOfMeasurementsProcessor implements WSResponseProcessor {

    @Autowired
    private Gson gson;

    @Autowired
    private JsonParser jsonParser;

    @Override
    public List<Object> process(String WSResponse) {

        JsonElement jsonElement = jsonParser.parse(WSResponse);
        JsonArray jsonLocalizations = jsonElement.getAsJsonArray();

        List<Object> pojoLocalizations = new LinkedList();
        for (JsonElement json : jsonLocalizations) {
            Object object = gson.fromJson(json, AirlyLocalizationOfMeasurements.class);
            pojoLocalizations.add(object);
        }

        return pojoLocalizations;
    }
}
