package pl.edu.agh.defsc.mails;

import com.mongodb.DBCollection;
import org.springframework.stereotype.Component;
import jdk.incubator.http.HttpResponse;
import java.util.*;

@Component
public class MailingAggregator {

    private Map<String, Integer> requestCounter = new HashMap<>();
    private Map<String, Map<Integer, Integer>> responseAggregation = new HashMap<>();

    public void aggregateResponseForCollection(HttpResponse<byte []> httpResponse, DBCollection collection) {
        String collectionName = collection.getName();

        if (responseAggregation.containsKey(collectionName)) {
            Map<Integer, Integer> mapForCollection = responseAggregation.get(collectionName);

            if (mapForCollection.containsKey(httpResponse.statusCode())) {
                Integer currentCounter = mapForCollection.get(httpResponse.statusCode());
                mapForCollection.put(httpResponse.statusCode(), new Integer(currentCounter + 1));
            } else {
                mapForCollection.put(httpResponse.statusCode(), new Integer(1));
            }
        } else {
            Map<Integer, Integer> mapForNewResponse = new HashMap<>();
            mapForNewResponse.put(httpResponse.statusCode(), new Integer(1));
            responseAggregation.put(collectionName, mapForNewResponse);
        }
    }

    public void registerNewRequest(DBCollection collection) {
        String collectionName = collection.getName();

        if (requestCounter.containsKey(collectionName)) {
            Integer howManyInserts = requestCounter.get(collectionName);
            requestCounter.put(collectionName, howManyInserts + 1);
        } else {
            requestCounter.put(collectionName, 1);
        }
    }

    public void clearAggregations() {
        requestCounter.clear();
        responseAggregation.clear();
    }

    public Map<String, Integer> getRequestCounter() {
        return requestCounter;
    }

    public Map<String, Map<Integer, Integer>> getResponseAggregation() {
        return responseAggregation;
    }
}
