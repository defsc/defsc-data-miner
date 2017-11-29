package pl.edu.agh.defsc.ws.deserializers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WSResponseDeserializer {
    List<Map> deserialize(String WSResponse) throws IOException;
}
