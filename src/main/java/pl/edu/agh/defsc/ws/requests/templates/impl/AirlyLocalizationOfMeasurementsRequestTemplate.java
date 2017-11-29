package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;

public class AirlyLocalizationOfMeasurementsRequestTemplate {
    private HttpGetTemplate template;
    private Environment environment;

    public AirlyLocalizationOfMeasurementsRequestTemplate(Environment environment) {
        this.environment = environment;
        template = new HttpGetTemplate();
    }

    public void setProtocol(String protocol) {
        template.setProtocol(protocol);
    }

    public void setDomain(String domain) {
        template.setDomain(domain);
    }

    public void setPath(String path) {
        template.setPath(path);
    }

    public void setUriParameter(String key, String value) {
        template.setUriParameter(key, value);
    }

    public void setHeaderParameter(String key, String value) {
        template.setHeaderParameter(key, value);
    }

    public void setAcceptType(String type) {
        template.setHeaderParameter("Accept", type);
    }

    public void setApiKey(String apiKey) {
        template.setHeaderParameter("apikey", apiKey);
    }

    public void setBbox(String southwestLat, String southwestLong, String northeastLat, String northeastLong) {
        setUriParameter("southwestLat", southwestLat);
        setUriParameter("southwestLong", southwestLong);
        setUriParameter("northeastLat", northeastLat);
        setUriParameter("northeastLong", northeastLong);
    }

    public void setDefaults() {
        setProtocol(environment.getProperty("airly.protocol"));
        setDomain(environment.getProperty("airly.host"));
        setPath(environment.getProperty("airly.sensors.path"));

        setBbox(
                environment.getProperty("airly.sensors.south.west.lat"),
                environment.getProperty("airly.sensors.south.west.lon"),
                environment.getProperty("airly.sensors.north.east.lat"),
                environment.getProperty("airly.sensors.north.east.lon")
        );

        setAcceptType("application/json");
        setApiKey(environment.getProperty("airly.apikey"));
    }

    public HttpRequest build() {
        return template.build();
    }
}
