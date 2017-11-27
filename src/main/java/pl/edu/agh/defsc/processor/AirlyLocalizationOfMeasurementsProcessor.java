package pl.edu.agh.defsc.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class AirlyLocalizationOfMeasurementsProcessor implements WSResponseProcessor {

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public List<Map> process(String WSResponse) throws IOException {
        List<Map> measurements = objectMapper.readValue(WSResponse, new TypeReference<List<Map>>(){});

        for (Map <String,Object> sensor : measurements)
        {
            sensor.put("_id", sensor.get("id"));
            sensor.remove("id");
        }

        return measurements;
    }
}
