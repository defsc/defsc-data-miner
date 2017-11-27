package pl.edu.agh.defsc.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HereTrafficFlowMeasurementsProcessor implements WSResponseProcessor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Map> process(String WSResponse) throws IOException {
        Map jsonMap = objectMapper.readValue(WSResponse,  Map.class);

        return Collections.singletonList(jsonMap);
    }
}
