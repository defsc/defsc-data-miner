package pl.edu.agh.defsc.ws.requests.builders;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.ws.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class AirlyLocalizationOfMeasurmentsRequestBuilder {
    private Environment environment;
    private HashMap<String, String> uriParameters;
    private HttpRequest.Builder requestBuilder;

    public AirlyLocalizationOfMeasurmentsRequestBuilder(Environment environment) {
        this.environment = environment;
        this.uriParameters = uriParameters = new HashMap<>();
        requestBuilder = HttpRequest.newBuilder();
    }

    public void headerParam(String key, String value) {
        requestBuilder.header(key, value);
    }

    public void uriParam(String key, String value) {
        uriParameters.put(key, value);
    }

    public HttpRequest build() throws URISyntaxException {
        URI uri = buildUri();

        requestBuilder.GET();

        requestBuilder.uri(uri);

        requestBuilder.header("Accept", "application/json");
        requestBuilder.header("apikey", environment.getProperty("airly.apikey"));

        return requestBuilder.build();
    }

    private URI buildUri() {
        URIBuilder uriBuilder = new URIBuilder();

        uriBuilder
                .protocol(environment.getProperty("airly.protocol"))
                .domain(environment.getProperty("airly.host"))
                .path(environment.getProperty("airly.sensors.path"));

        readBboxFromConfig();
        uriParameters.entrySet().
                forEach(paramEntry -> uriBuilder.uriParam(paramEntry.getKey(), paramEntry.getValue()));

        return uriBuilder.build();
    }

    private void readBboxFromConfig() {
        uriParameters.put("southwestLat", environment.getProperty("airly.sensors.south.west.lat"));
        uriParameters.put("southwestLong", environment.getProperty("airly.sensors.south.west.lon"));
        uriParameters.put("northeastLat", environment.getProperty("airly.sensors.north.east.lat"));
        uriParameters.put("northeastLong", environment.getProperty("airly.sensors.north.east.lon"));
    }
}

