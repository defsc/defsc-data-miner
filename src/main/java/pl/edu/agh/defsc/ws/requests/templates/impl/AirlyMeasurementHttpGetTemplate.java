package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;


public class AirlyMeasurementHttpGetTemplate implements WSHttpGetTemplate {
    private HttpGetTemplate template;
    private Environment environment;

    public AirlyMeasurementHttpGetTemplate(Environment environment) {
        this.environment = environment;
        this.template = new HttpGetTemplate();
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

    public void setSensorId(String sensorId) {
        template.setUriParameter("sensorId", sensorId);
    }

    public void setAcceptType(String type) {
        template.setHeaderParameter("Accept", type);
    }

    public void setApiKey(String apiKey) {
        template.setHeaderParameter("apikey", apiKey);
    }

    public void setDefaults() {
        setProtocol(environment.getProperty("airly.protocol"));
        setDomain(environment.getProperty("airly.host"));
        setPath(environment.getProperty("airly.measurements.path"));

        setAcceptType("application/json");
        setApiKey(environment.getProperty("airly.apikey"));
    }

    public void customize(LocalizationOfMeasurements lom) {
        setSensorId((String) lom.getId());
    }

    public HttpRequest build() {
        return template.build();
    }
}
