package pl.edu.agh.defsc.ws.requests.builders;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.ws.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HereTrafficFlowRequestBuilder {
    private Environment environment;
    private HashMap<String, String> uriParameters;
    private HttpRequest.Builder requestBuilder;

    public HereTrafficFlowRequestBuilder(Environment environment) {
        this.environment = environment;
        this.uriParameters = uriParameters = new HashMap<>();
        requestBuilder = HttpRequest.newBuilder();
    }

    public void proximity(double lat, double lon, int radious) {
        String proximation = lat + "," + lon + "," + radious;
        requestBuilder.header("prox", proximation);
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
        requestBuilder.header("app_id", environment.getProperty("here.app_id"));
        requestBuilder.header("app_code", environment.getProperty("here.app_code"));

        return requestBuilder.build();
    }

    private URI buildUri() {
        URIBuilder uriBuilder = new URIBuilder();

        uriBuilder
                .protocol(environment.getProperty("here.protocol"))
                .domain(environment.getProperty("here.host"))
                .path(environment.getProperty("here.traffic.flow.path"));

        uriParameters.entrySet().
                forEach(paramEntry -> uriBuilder.uriParam(paramEntry.getKey(), paramEntry.getValue()));

        return uriBuilder.build();
    }
}

