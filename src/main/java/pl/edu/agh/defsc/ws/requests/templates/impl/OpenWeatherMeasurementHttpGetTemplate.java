package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;

public class OpenWeatherMeasurementHttpGetTemplate implements WSHttpGetTemplate {
    private HttpGetTemplate template;
    private Environment environment;

    public OpenWeatherMeasurementHttpGetTemplate(Environment environment) {
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

    public void setLon(String longitude) {
        template.setUriParameter("lon", longitude);
    }

    public void setLat(String latitude) {
        template.setUriParameter("lat", latitude);
    }

    public void setAppId(String appId) {
        template.setUriParameter("APPID", appId);
    }

    public void setDefaults() {
        setProtocol(environment.getProperty("open.weather.protocol"));
        setDomain(environment.getProperty("open.weather.domain"));
        setPath(environment.getProperty("open.weather.path"));

        setAppId(environment.getProperty("open.weather.APPID"));

        setAcceptType("application/json");
    }

    public void customize(LocalizationOfMeasurements lom) {
        setLat(Double.toString(lom.getLocalization().getLat()));
        setLon(Double.toString(lom.getLocalization().getLon()));
    }

    public HttpRequest build() {
        return template.build();
    }
}
