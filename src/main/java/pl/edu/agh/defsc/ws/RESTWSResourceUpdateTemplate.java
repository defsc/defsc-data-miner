package pl.edu.agh.defsc.ws;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;
import pl.edu.agh.defsc.ws.requests.templates.WSHttpGetTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RESTWSResourceUpdateTemplate {

    @Autowired
    private HttpClient httpClient;

    public void update(DBCollection collection,
                       WSHttpGetTemplate requestTemplate,
                       WSResponseDeserializer deserializer,
                       List<? extends LocalizationOfMeasurements> loms,
                       Integer delay) {

        requestTemplate.setDefaults();

        for (LocalizationOfMeasurements lom : loms) {
            HttpRequest request = buildRequest(requestTemplate, lom);
            HttpResponse<String> response = executeRequest(request);
            List<Map> items = deserializeResponse(response, deserializer);
            updateDatabase(items, collection);
            performOverloadProtection(delay);
        }
    }

    public HttpRequest buildRequest(WSHttpGetTemplate template, LocalizationOfMeasurements lom) {
        template.customize(lom);
        return template.build();
    }

    public HttpResponse executeRequest(HttpRequest request) {
        System.out.println(request);
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());
            System.out.println(httpResponse.body() + " Response code:" + httpResponse.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return httpResponse;
    }

    public List<Map> deserializeResponse(HttpResponse<String> response, WSResponseDeserializer deserializer) {
        List<Map> items = null;
        try {
            items = deserializer.deserialize(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void updateDatabase(List<Map> items, DBCollection collection) {
        for (Map item : items) {
            collection.save(new BasicDBObject(item));
        }
    }


    public void performOverloadProtection(Integer delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // loger warn
            e.printStackTrace();
        }
    }


}

