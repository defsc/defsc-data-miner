package pl.edu.agh.defsc.ws;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ScheduledTasks;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.exceptions.ResourceUpdateException;
import pl.edu.agh.defsc.mails.MailingFacade;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RestResourceUpdateTemplate {
    private final static Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private MailingFacade mailingFacade;

    public void update(DBCollection collection, WSHttpGetTemplate requestTemplate,
                       WSResponseDeserializer deserializer, List<? extends LocalizationOfMeasurements> loms,
                       Integer delay) {

        requestTemplate.setDefaults();

        for (LocalizationOfMeasurements lom : loms) {
            HttpRequest request = buildRequest(requestTemplate, lom);
            try {
                mailingFacade.handleNewRequest(collection);
                HttpResponse<byte []> response = executeRequest(request);
                mailingFacade.handleNewResponse(response, collection);
                List<Map> items = deserializeResponse(response, deserializer);
                addLomIdToItems(items, lom);
                updateDatabase(items, collection);
            } catch (ResourceUpdateException e) {
                log.warn("Processing of resource was interupted", e);
            }
            performOverloadProtection(delay);
        }
    }

    public HttpRequest buildRequest(WSHttpGetTemplate template, LocalizationOfMeasurements lom) {
        template.customize(lom);
        return template.build();
    }

    public HttpResponse executeRequest(HttpRequest request) throws ResourceUpdateException {
        HttpResponse<byte []> httpResponse = null;
        try {
            log.info("Executing http request using {}", request);
            httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asByteArray());
            log.info("Request was executed, reponse status code: {}", httpResponse.statusCode());
        } catch (InterruptedException | IOException e) {
            throw new ResourceUpdateException("Exception during executing http request:" + request, e);
        }

        return httpResponse;
    }

    public List<Map> deserializeResponse(HttpResponse<byte []> response, WSResponseDeserializer deserializer) {
        return deserializer.deserialize(response);
    }

    public void addLomIdToItems(List<Map> items, LocalizationOfMeasurements lom) {
        for (Map item : items) {
            item.put("lom_id", lom.getId());
        }
    }

    public void updateDatabase(List<Map> items, DBCollection collection) {
        for (Map item : items) {
            DBObject toSave = new BasicDBObject(item);
            log.debug("Saving object into database {}", toSave);
            collection.save(toSave);
        }
    }

    public void performOverloadProtection(Integer delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("Overload delay failed {}", e);
            throw new RuntimeException("Overload delay failed", e);
        }
    }
}

