package pl.edu.agh.defsc.ws.deserializers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class AirlyLocalizationOfMeasurementsDeserializer implements WSResponseDeserializer {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Map> deserialize(String WSResponse) throws IOException {
        List<Map> sensors = mapper.readValue(WSResponse, new TypeReference<List<Map>>() {
        });

        for (Map<String, Object> sensor : sensors) {
            sensor.put("_id", sensor.get("id"));
            sensor.remove("id");
        }

        return sensors;
    }
}
