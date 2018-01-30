package pl.edu.agh.defsc;

import com.mongodb.DBCollection;
import jdk.incubator.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.edu.agh.defsc.entity.localizations.impl.AirlySensor;
import pl.edu.agh.defsc.entity.localizations.impl.WundergroundSensor;
import pl.edu.agh.defsc.mails.MailingFacade;
import pl.edu.agh.defsc.ws.RestResourceUpdateTemplate;
import pl.edu.agh.defsc.ws.deserializers.impl.AirlyLocalizationOfMeasurementsDeserializer;
import pl.edu.agh.defsc.ws.deserializers.impl.AirlyMeasurementDeserializer;
import pl.edu.agh.defsc.ws.deserializers.impl.SimpleWSResponseDeserializer;
import pl.edu.agh.defsc.ws.requests.templates.impl.AirlyMeasurementHttpGetTemplate;
import pl.edu.agh.defsc.ws.requests.templates.impl.HereTrafficFlowMeasurementHttpGetTemplate;
import pl.edu.agh.defsc.ws.requests.templates.impl.OpenWeatherMeasurementHttpGetTemplate;
import pl.edu.agh.defsc.ws.requests.templates.impl.WundergroundWeatherMeasurementHttpGetTemplate;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private final static Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

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
    SimpleWSResponseDeserializer wsResponseDeserializer;

    @Autowired
    RestResourceUpdateTemplate processingTemplate;

    @Autowired
    private MailingFacade mailingFacade;

    @Scheduled(fixedRate = 86400000)
    public void updateAirlyMeasurements()  {
        log.info("Update airly measurements start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("airly.air.pullution.measurements.collection.name"));
        AirlyMeasurementHttpGetTemplate template = new AirlyMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("airly.air.pollution.sensors.collection.name"));

        processingTemplate.update(measurements, template, amDeserializer, loms, Integer.parseInt(environment.getProperty("airly.requests.delay")));

        log.info("Update airly measurements end");
    }


    @Scheduled(fixedRate = 3600000)
    public void updateTrafficFLowItems() {
        log.info("Update traffic flow items start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("traffic.measurements.collection.name"));
        HereTrafficFlowMeasurementHttpGetTemplate template = new HereTrafficFlowMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("airly.air.pollution.sensors.collection.name"));

        processingTemplate.update(measurements, template, wsResponseDeserializer, loms, Integer.parseInt(environment.getProperty("here.requests.delay")));

        log.info("Update traffic flow items end");
    }



    @Scheduled(fixedRate = 3600000)
    public void updateOpenWeatherMeasurements() {
        log.info("Update open weather items start");

        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("weather.measurements.collection.name"));
        OpenWeatherMeasurementHttpGetTemplate template = new OpenWeatherMeasurementHttpGetTemplate(environment);
        List<AirlySensor> loms = mongoTemplate.findAll(AirlySensor.class, environment.getProperty("airly.air.pollution.sensors.collection.name"));

        processingTemplate.update(measurements, template, wsResponseDeserializer, loms, Integer.parseInt(environment.getProperty("open.weather.requests.delay")));

        log.info("Update open weather items end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateWundergroundWeatherMeasurements_1() {
        log.info("Update wunder weather items 1 start");


        DBCollection wwMeasurements = mongoTemplate.getCollection(environment.getProperty("wunderground.measurements.collection.name"));
        WundergroundWeatherMeasurementHttpGetTemplate template = new WundergroundWeatherMeasurementHttpGetTemplate(environment, environment.getProperty("wunderground.weather.apikey_1"));
        List<WundergroundSensor> loms = mongoTemplate.findAll(WundergroundSensor.class, environment.getProperty("wunderground.sensors_1.collection.name"));

        processingTemplate.update(wwMeasurements, template, wsResponseDeserializer, loms, Integer.parseInt(environment.getProperty("wunderground.requests.delay")));

        log.info("Update wunder weather items 1 end");
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 100)
    public void updateWundergroundWeatherMeasurements_2() {
        log.info("Update wunder weather items 2 start");


        DBCollection wwMeasurements = mongoTemplate.getCollection(environment.getProperty("wunderground.measurements.collection.name"));
        WundergroundWeatherMeasurementHttpGetTemplate template = new WundergroundWeatherMeasurementHttpGetTemplate(environment, environment.getProperty("wunderground.weather.apikey_2"));
        List<WundergroundSensor> loms = mongoTemplate.findAll(WundergroundSensor.class, environment.getProperty("wunderground.sensors_2.collection.name"));

        processingTemplate.update(wwMeasurements, template, wsResponseDeserializer, loms, Integer.parseInt(environment.getProperty("wunderground.requests.delay")));

        log.info("Update wunder weather items 2 end");
    }

    @Scheduled(fixedRate = 14400000, initialDelay = 120000)
    public void sendMail() {
        System.out.println("Send mails start");

        mailingFacade.sendDailyMails();

        System.out.println("Send mails end");
    }

    /*
    @Scheduled(fixedRate = 86400000)
    public void updateAirlySensors() throws IOException, URISyntaxException, InterruptedException {
        log.info("Update airly sensor start");


        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        AirlyLocalizationOfMeasurementsRequestTemplate requestTemplate = new AirlyLocalizationOfMeasurementsRequestTemplate(environment);
        requestTemplate.setDefaults();

        HttpRequest request = requestTemplate.build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandler.asString());

        List<Map> items = alomDeserializer.deserialize(httpResponse.body());

        for (Map item : items) {
            sensors.save(new BasicDBObject(item));
        }

        log.info("Update airly sensor end");
    }
    */

}
