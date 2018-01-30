package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.entity.localizations.impl.WundergroundSensor;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;

public class WundergroundWeatherMeasurementHttpGetTemplate implements WSHttpGetTemplate {
    private HttpGetTemplate template;
    private Environment environment;
    private String apikey;

    public WundergroundWeatherMeasurementHttpGetTemplate(Environment environment, String apikey) {
        this.environment = environment;
        template = new HttpGetTemplate();
        this.apikey = apikey;
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

    public void setDefaults() {
        setProtocol(environment.getProperty("wunderground.weather.protocol"));
        setDomain(environment.getProperty("wunderground.weather.domain"));

        setAcceptType("application/json");
    }

    public void customize(LocalizationOfMeasurements lom) {
        WundergroundSensor wundergroundSensor = (WundergroundSensor) lom;
        String path =
                environment.getProperty("wunderground.weather.path.first.part") +
                        apikey +
                        environment.getProperty("wunderground.weather.path.second.part") +
                        wundergroundSensor.getType() + ":" +
                        wundergroundSensor.getId() + ".json";

        setPath(path);
    }

    public HttpRequest build() {
        return template.build();
    }
}
