package pl.edu.agh.defsc;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.edu.agh.defsc.entity.localizations.AirlyLocalizationOfMeasurements;
import pl.edu.agh.defsc.processor.AirlyLocalizationOfMeasurementsProcessor;
import pl.edu.agh.defsc.ws.requests.builders.AirlyLocalizationOfMeasurmentsRequestBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    @Autowired
    private Environment environment;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    AirlyLocalizationOfMeasurementsProcessor airlyLocalizationOfMeasurementsProcessor;

    @Scheduled(fixedRate = 86400000)
    public void updateTrafficFLowItems() throws IOException, URISyntaxException, InterruptedException {
/*
        List<AirlyLocalizationOfMeasurements> localizationsOfMeasurements =
                mongoTemplate.findAll(AirlyLocalizationOfMeasurements.class);

        for (LocalizationOfMeasurements localizationOfMeasurements : localizationsOfMeasurements) {
            HereTrafficFlowRequestBuilder requestBuilder = new HereTrafficFlowRequestBuilder(environment);

            requestBuilder.proximity(
                    localizationOfMeasurements.getGeoPoint().getLat(),
                    localizationOfMeasurements.getGeoPoint().getLon(),
                    1000
            );

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

            List<TrafficFlowItem> items = trafficFlowRequestProcessor(httpResponse.body());
            for (TrafficFlowItem item : items) {
                mongoTemplate.save(item);
            }

        }
*/
    }

    @Scheduled(fixedRate = 86400000)
    public void updateAirly() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Start");

        AirlyLocalizationOfMeasurmentsRequestBuilder requestBuilder = new AirlyLocalizationOfMeasurmentsRequestBuilder(environment);

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

        String response = httpResponse.body();
        //System.out.println(response);

        List<Object> items = airlyLocalizationOfMeasurementsProcessor.process(response);

        for (Object item : items) {
            //System.out.println(item);
            mongoTemplate.save((AirlyLocalizationOfMeasurements) item);
        }

        System.out.println("End");
    }
}
