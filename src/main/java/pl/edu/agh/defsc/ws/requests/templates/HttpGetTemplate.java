package pl.edu.agh.defsc.ws.requests.templates;

import jdk.incubator.http.HttpRequest;
import pl.edu.agh.defsc.ws.utils.URIBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpGetTemplate {
    private String protocol;
    private String domain;
    private String path;
    private Map<String, String> uriParameters = new HashMap<>();
    private Map<String, String> headerParametrs = new HashMap<>();

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUriParameter(String key, String value) {
        uriParameters.put(key, value);
    }

    public void setHeaderParameter(String key, String value) {
        headerParametrs.put(key, value);
    }

    public HttpRequest build() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();

        builder.GET();
        builder.uri(buildUri());

        headerParametrs.entrySet().forEach(k_v -> builder.header(k_v.getKey(), k_v.getValue()));

        return builder.build();
    }

    private URI buildUri() {
        URIBuilder uriBuilder = new URIBuilder();

        uriBuilder
                .protocol(protocol)
                .domain(domain)
                .path(path);

        uriParameters.entrySet().
                forEach(paramEntry -> uriBuilder.uriParam(paramEntry.getKey(), paramEntry.getValue()));

        return uriBuilder.build();
    }
}
