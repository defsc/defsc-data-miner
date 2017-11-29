package pl.edu.agh.defsc.ws.deserializers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class AirlyMeasurementDeserializer implements WSResponseDeserializer {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Map> deserialize(String WSResponse) throws IOException {
        Map map = mapper.readValue(WSResponse, Map.class);

        List<Map> historyOfMeasurements = (List) map.get("history");

        return historyOfMeasurements;
    }
}
