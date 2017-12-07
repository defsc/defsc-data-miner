package pl.edu.agh.defsc.ws.deserializers;

import jdk.incubator.http.HttpResponse;

import java.util.List;
import java.util.Map;


public interface WSResponseDeserializer {
    public List<Map> deserialize(HttpResponse<byte []> WSResponse);
}
