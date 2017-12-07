package pl.edu.agh.defsc.ws.deserializers.impl;

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
public class SimpleWSResponseDeserializer implements WSResponseDeserializer {
    private final static Logger log = LoggerFactory.getLogger(WSResponseDeserializer.class);

    @Autowired
    private ObjectMapper mapper;

    public List<Map> deserialize(HttpResponse<byte[]> WSResponse) {
        return tryToDeserialize(WSResponse);
    }

    public List<Map> tryToDeserialize(HttpResponse<byte []> WSResponse) {
        try {
            return Collections.singletonList(mapper.readValue(WSResponse.body(), Map.class));
        } catch (IOException e) {
            log.warn("Unable to deserialize following response {}", WSResponse.body());
            return Collections.emptyList();
        }
    }
}
