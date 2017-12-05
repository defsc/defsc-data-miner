package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;

public class WiosMeasurementHttpGetTemplate implements WSHttpGetTemplate {
    private HttpGetTemplate template;
    private Environment environment;

    public WiosMeasurementHttpGetTemplate(Environment environment) {
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

    public void setId(String id) { template.setUriParameter("id",id);}

    public void setDefaults() {
        setProtocol(environment.getProperty("wios.protocol"));
        setDomain(environment.getProperty("wios.domain"));
        setPath(environment.getProperty("wios.path"));

        setUriParameter("param","AQI");
        setAcceptType("application/json");
    }

    public void customize(LocalizationOfMeasurements lom) {
        setId((String)lom.getId());
    }

    public HttpRequest build() {
        return template.build();
    }
}
