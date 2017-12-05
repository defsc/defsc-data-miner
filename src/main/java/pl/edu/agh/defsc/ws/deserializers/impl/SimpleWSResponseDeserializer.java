package pl.edu.agh.defsc.ws.deserializers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SimpleWSResponseDeserializer implements WSResponseDeserializer {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Map> deserialize(String WSResponse) throws IOException {
        Map jsonMap = mapper.readValue(WSResponse, Map.class);

        return Collections.singletonList(jsonMap);
    }
}
