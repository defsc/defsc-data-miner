package pl.edu.agh.defsc.ws.deserializers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AirlyLocalizationOfMeasurementsDeserializer implements WSResponseDeserializer {
    private final static Logger log = LoggerFactory.getLogger(WSResponseDeserializer.class);

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Map> deserialize(HttpResponse<byte []> WSResponse) {
        List<Map> sensors = tryToDeserialize(WSResponse);

        for (Map<String, Object> sensor : sensors) {
            sensor.put("_id", sensor.get("id"));
            sensor.remove("id");
        }

        return sensors;
    }

    public List<Map> tryToDeserialize(HttpResponse<byte[]> WSResponse) {
        try {
            return mapper.readValue(WSResponse.body(), new TypeReference<List<Map>>() {});
        } catch (IOException e) {
            log.warn("Unable to deserialize following response {}", WSResponse.body());
            return Collections.emptyList();
        }
    }
}
