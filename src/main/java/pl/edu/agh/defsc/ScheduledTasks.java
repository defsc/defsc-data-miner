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
import pl.edu.agh.defsc.entity.localizations.impl.AirlySensor;
import pl.edu.agh.defsc.entity.localizations.impl.WundergroundSensor;
import pl.edu.agh.defsc.ws.RESTWSResourceUpdateTemplate;
import pl.edu.agh.defsc.ws.deserializers.impl.*;
import pl.edu.agh.defsc.ws.requests.templates.impl.*;

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
    AirlyLocalizationOfMeasurementsDeserializer alomDeserializer;

    @Autowired
    AirlyMeasurementDeserializer amDeserializer;

    @Autowired
    OpenWeatherMeasurementDeserializer owmDeserializer;

    @Autowired
    HereTrafficFlowMeasurementDeserializer htfmDeserializer;

    @Autowired
    WudnergroundWeatherDeserializer wwDeserializer;

    @Autowired
    RESTWSResourceUpdateTemplate processingTemplate;

    @Scheduled(fixedRate = 86400000)
    public void updateAirlySensors() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update airly sensor start");


        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        AirlyLocalizationOfMeasurementsRequestTemplate requestTemplate = new AirlyLocalizationOfMeasurementsRequestTemplate(environment);
        requestTemplate.setDefaults();

        HttpRequest request = requestTemplate.build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

        List<Map> items = alomDeserializer.deserialize(httpResponse.body());

        for (Map item : items) {
            sensors.save(new BasicDBObject(item));
        }


        System.out.println("Update airly sensor end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateAirlyMeasurements()  {
        System.out.println("Update airly measurements start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("air.pullution.measurements.collection.name"));
        AirlyMeasurementHttpGetTemplate template = new AirlyMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        processingTemplate.update(measurements, template, amDeserializer, loms, Integer.parseInt(environment.getProperty("airly.requests.delay")));

        System.out.println("Update airly measurements end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateTrafficFLowItems() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update traffic flow items start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("traffic.measurements.collection.name"));
        HereTrafficFlowMeasurementHttpGetTemplate template = new HereTrafficFlowMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        processingTemplate.update(measurements, template, htfmDeserializer, loms, Integer.parseInt(environment.getProperty("here.requests.delay")));

        System.out.println("Update traffic flow items start");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateOpenWeatherMeasurements() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update open weather items start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("weather.measurements.collection.name"));
        OpenWeatherMeasurementHttpGetTemplate template = new OpenWeatherMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("air.pollution.sensors.collection.name")).subList(0, 5);

        processingTemplate.update(measurements, template, owmDeserializer, loms, Integer.parseInt(environment.getProperty("open.weather.requests.delay")));

        System.out.println("Update open weather items end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateWundergroundWeatherMeasurements() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Update wunder weather items start");

        DBCollection wwMeasurements = mongoTemplate.getCollection(environment.getProperty("wunderground.measurements.collection.name"));
        WundergroundWeatherMeasurementHttpGetTemplate template = new WundergroundWeatherMeasurementHttpGetTemplate(environment, environment.getProperty("wunderground.weather.apikey"));
        List<WundergroundSensor> loms = mongoTemplate.findAll(WundergroundSensor.class, environment.getProperty("wunderground.stations1.collection.name")).subList(0, 5);

        processingTemplate.update(wwMeasurements, template, wwDeserializer, loms, Integer.parseInt(environment.getProperty("wunderground.requests.delay")));

        System.out.println("Update wunder weather items end");
    }
}
