package pl.edu.agh.defsc.ws.requests.templates;

import jdk.incubator.http.HttpRequest;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;

public interface WSHttpGetTemplate {
    void setDefaults();

    void customize(LocalizationOfMeasurements lom);

    HttpRequest build();
}
