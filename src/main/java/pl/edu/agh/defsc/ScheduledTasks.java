package pl.edu.agh.defsc;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.edu.agh.defsc.entity.localizations.AirlySensor;
import pl.edu.agh.defsc.entity.localizations.LocalizationOfMeasurements;
import pl.edu.agh.defsc.processor.AirlyLocalizationOfMeasurementsProcessor;
import pl.edu.agh.defsc.processor.AirlyMeasurementsProcessor;
import pl.edu.agh.defsc.processor.HereTrafficFlowMeasurementsProcessor;
import pl.edu.agh.defsc.processor.OpenWeatherMeasurementsProcessor;
import pl.edu.agh.defsc.ws.requests.templates.AirlyMeasurementHttpGetTemplate;
import pl.edu.agh.defsc.ws.requests.templates.AirlySensorsRequestTemplate;
import pl.edu.agh.defsc.ws.requests.templates.HERETrafficHttpGetTemplate;
import pl.edu.agh.defsc.ws.requests.templates.OpenWeatherHttpGetTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

    @Autowired
    AirlyMeasurementsProcessor airlyMeasurementsProcessor;

    @Autowired
    OpenWeatherMeasurementsProcessor openWeatherMeasurementsProcessor;

    @Autowired
    HereTrafficFlowMeasurementsProcessor hereTrafficFlowMeasurementsProcessor;

    @Scheduled(fixedRate = 86400000)
    public void updateAirlySensors() throws IOException, URISyntaxException, InterruptedException {

        System.out.println("Update airly sensor start");


        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        AirlySensorsRequestTemplate requestTemplate = new AirlySensorsRequestTemplate(environment);
        requestTemplate.setDefaults();

        HttpRequest request = requestTemplate.build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

        List<Map> items = airlyLocalizationOfMeasurementsProcessor.process(httpResponse.body());

        for (Map item : items) {
            sensors.save(new BasicDBObject(item));
        }


        System.out.println("Update airly sensor end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateAirlyMeasurements() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update airly measurements start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("air.pullution.measurements.collection.name"));
        AirlyMeasurementHttpGetTemplate template = new AirlyMeasurementHttpGetTemplate(environment);
        template.setDefaults();

        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        for (LocalizationOfMeasurements lom : loms) {
            System.out.println("Loop start");
            template.setSensorId((String)lom.getId());
            HttpRequest request = template.build();
            System.out.println("Request: " + request  );
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

            System.out.println(httpResponse.body() + " Response code:" + httpResponse.statusCode());
            List<Map> items = airlyMeasurementsProcessor.process(httpResponse.body());

            for (Map item : items) {
                measurements.save(new BasicDBObject(item));
            }

            Thread.sleep(2000);
        }

        System.out.println("Update airly measurements end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateTrafficFLowItems() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update traffic flow items start");


        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("traffic.measurements.collection.name"));
        HERETrafficHttpGetTemplate template = new HERETrafficHttpGetTemplate(environment);
        template.setDefaults();

        System.out.println("Before fetching lom");

        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        System.out.println("After fetching lom:" + loms.size());

        for (LocalizationOfMeasurements lom : loms) {
            System.out.println("Loop start");
            template.setProx(lom.getLocalization().getLat(), lom.getLocalization().getLon(), 1000.0);
            HttpRequest request = template.build();
            System.out.println("Request: " + request  );
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

            System.out.println(httpResponse.body() + " Response code:" + httpResponse.statusCode());
            List<Map> items = hereTrafficFlowMeasurementsProcessor.process(httpResponse.body());

            for (Map item : items) {
                measurements.save(new BasicDBObject(item));
            }

            Thread.sleep(2000);
        }

        System.out.println("Update traffic flow items start");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateOpenWeatherMeasurements() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update open weather items start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("weather.measurements.collection.name"));
        OpenWeatherHttpGetTemplate template = new OpenWeatherHttpGetTemplate(environment);
        template.setDefaults();

        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        for (LocalizationOfMeasurements lom : loms) {
            System.out.println("Loop start");
            template.setLat(Double.toString(lom.getLocalization().getLat()));
            template.setLon(Double.toString(lom.getLocalization().getLon()));
            HttpRequest request = template.build();
            System.out.println("Request: " + request  );
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

            System.out.println(httpResponse.body() + " Response code:" + httpResponse.statusCode());
            List<Map> items = openWeatherMeasurementsProcessor.process(httpResponse.body());

            for (Map item : items) {
                measurements.save(new BasicDBObject(item));
            }

            Thread.sleep(2000);

        }


        System.out.println("Update open weather items end");
    }
}
