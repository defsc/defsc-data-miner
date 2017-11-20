package pl.edu.agh.defsc.ws.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class URIBuilder {
    private String protocol;
    private String domain;
    private String path;
    private Map<String, String> uriParameters;

    public URIBuilder() {
        uriParameters = new HashMap<>();
    }

    public URIBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public URIBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public URIBuilder path(String path) {
        this.path = path;
        return this;
    }

    public URIBuilder uriParam(String key, String value) {
        uriParameters.put(key, value);
        return this;
    }

    public URI build() {
        String uriString = buildURIString();
        URI uri = null;

        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private String buildURIString() {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol)
                .append(domain)
                .append(path);

        for (Map.Entry<String, String> entry : uriParameters.entrySet()) {
            sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }

        if (uriParameters.size() > 0) {
            int firstAmpersandIndex = sb.indexOf("&");
            sb.replace(firstAmpersandIndex, firstAmpersandIndex + 1, "?");
        }

        return sb.toString();
    }


}
