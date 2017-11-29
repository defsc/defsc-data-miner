package pl.edu.agh.defsc.ws.requests.templates.impl;

import jdk.incubator.http.HttpRequest;
import org.springframework.core.env.Environment;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;


public class HereTrafficFlowMeasurementHttpGetTemplate implements WSHttpGetTemplate {
    private HttpGetTemplate template;
    private Environment environment;

    public HereTrafficFlowMeasurementHttpGetTemplate(Environment environment) {
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

    public void setAppId(String appId) {
        template.setUriParameter("app_id", appId);
    }

    public void setAppCode(String appCode) {
        template.setUriParameter("app_code", appCode);
    }

    public void setProx(double lat, double lon, double range) {
        String prox = lat + "," + lon + "," + range;
        template.setUriParameter("prox", prox);
    }

    public void setDefaults() {
        setProtocol(environment.getProperty("here.protocol"));
        setDomain(environment.getProperty("here.host"));
        setPath(environment.getProperty("here.traffic.flow.path"));

        setAppId(environment.getProperty("here.app_id"));
        setAppCode(environment.getProperty("here.app_code"));

        setAcceptType("application/json");
    }

    public void customize(LocalizationOfMeasurements lom) {
        setProx(lom.getLocalization().getLat(), lom.getLocalization().getLon(), 1000.0);
    }

    public HttpRequest build() {
        return template.build();
    }
}
