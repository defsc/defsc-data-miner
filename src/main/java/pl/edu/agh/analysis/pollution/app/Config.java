package pl.edu.agh.analysis.pollution.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.edu.agh.analysis.pollution.utils.RequestsCounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("pl.edu.agh.analysis.pollution.utils")
@EnableScheduling
public class Config {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private Gson gson;

    @Autowired
    private RequestsCounter requestsCounter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRate=86400000)
    public void oncePerDay() throws IOException {
        logger.info("Once per day scheduled task start");
        updateSensorList();
        updateAirPollutionMeasurements();
        logger.info("Once per day scheduled task end");
    }

    @Scheduled(fixedRate=3600000)
    public void oncePerHour() throws IOException {
        logger.info("Once per hour scheduled task start");
        Instant now = Instant.now();
        updateWeatherMeasurements(now);
        updateTrafficFlowMeasurements(now);
        System.out.println(requestsCounter);
        logger.info("Once per hour scheduled task end");
    }

    private void updateSensorList() throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));

        Map<String, String> URLParameters = new HashMap<>();
        URLParameters.put("southwestLat", environment.getProperty("airly.sensors.south.west.lat"));
        URLParameters.put("southwestLong", environment.getProperty("airly.sensors.south.west.lon"));
        URLParameters.put("northeastLat", environment.getProperty("airly.sensors.north.east.lat"));
        URLParameters.put("northeastLong", environment.getProperty("airly.sensors.north.east.lon"));

        String url = buildURL(
                environment.getProperty("airly.protocol"),
                environment.getProperty("airly.host"),
                environment.getProperty("airly.sensors.path"),
                URLParameters
        );

        Map <String, String> headerParameters = new HashMap<>();
        headerParameters.put("apikey", environment.getProperty("airly.apikey"));

        JsonElement response = executeHttpRequest(url, headerParameters);
        JsonArray results = response.getAsJsonArray();

        for (JsonElement result  : results) {
            DBObject dbObject = (DBObject) JSON.parse(result.toString());

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("id", dbObject.get("id"));
            DBCursor cursor = sensors.find(whereQuery);

            if (cursor.size() == 0) {
                sensors.insert(dbObject);
                logger.debug(result.toString());
            }
        }
    }

    private void updateAirPollutionMeasurements() throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("air.pullution.measurements.collection.name"));
        DBCursor cursor = sensors.find();

        while(cursor.hasNext()) {
            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(id + " "  +  longitude + " " + latitude);

            Map<String, String> URLParameters = new HashMap<>();
            URLParameters.put("sensorId", id);

            String url = buildURL(
                    environment.getProperty("airly.protocol"),
                    environment.getProperty("airly.host"),
                    environment.getProperty("airly.measurements.path"),
                    URLParameters
            );

            Map <String, String> headerParameters = new HashMap<>();
            headerParameters.put("apikey", environment.getProperty("airly.apikey"));

            JsonElement element = executeHttpRequest(url, headerParameters);
            JsonArray results = element.getAsJsonObject().getAsJsonArray("history");

            for (JsonElement result  : results) {
                DBObject dbObject = (DBObject) JSON.parse(result.toString());
                dbObject.put("sensor_id", id);
                dbObject.put("longitude", longitude);
                dbObject.put("latitude", latitude);
                measurements.insert(dbObject);
                logger.debug(result.toString());
            }

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateWeatherMeasurements(Instant now) throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        DBCollection weatherMeasurements = mongoTemplate.getCollection(environment.getProperty("weather.measurements.collection.name"));
        DBCursor cursor = sensors.find();

        while(cursor.hasNext()) {
            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(id + " "  +  longitude + " " + latitude);

            Map<String, String> URLParameters = new HashMap<>();
            URLParameters.put("lat", latitude);
            URLParameters.put("lon", longitude);
            URLParameters.put("APPID", environment.getProperty("open.weather.APPID"));

            String url = buildURL(
                    environment.getProperty("open.weather.protocol"),
                    environment.getProperty("open.weather.host"),
                    environment.getProperty("open.weather.path"),
                    URLParameters
            );

            Map <String, String> headerParameters = new HashMap<>();
            headerParameters.put("apikey", environment.getProperty("airly.apikey"));

            JsonElement element = executeHttpRequest(url, headerParameters);
            DBObject weatherMeasurementDbObject = (DBObject) JSON.parse(element.toString());

            weatherMeasurementDbObject.put("timestamp", now.toString());
            weatherMeasurementDbObject.put("longitude", longitude);
            weatherMeasurementDbObject.put("latitude", latitude);

            weatherMeasurements.insert(weatherMeasurementDbObject);

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTrafficFlowMeasurements(Instant now) throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        DBCollection trafficMeasurements = mongoTemplate.getCollection(environment.getProperty("traffic.measurements.collection.name"));
        DBCursor cursor = sensors.find();

        while(cursor.hasNext()) {
            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(id + " "  +  longitude + " " + latitude);

            String location = latitude + "," + longitude  + "," + 1000;
            Map<String, String> URLParameters = new HashMap<>();
            URLParameters.put("app_id", environment.getProperty("here.app_id"));
            URLParameters.put("app_code", environment.getProperty("here.app_code"));
            URLParameters.put("prox", location);

            String url = buildURL(
                    environment.getProperty("here.protocol"),
                    environment.getProperty("here.host"),
                    environment.getProperty("here.traffic.flow.path"),
                    URLParameters
            );

            Map <String, String> headerParameters = new HashMap<>();
            JsonElement element = executeHttpRequest(url, headerParameters);
            DBObject trafficMeasurementDbObject = (DBObject) JSON.parse(element.toString());

            trafficMeasurementDbObject.put("timestamp", now.toString());
            trafficMeasurementDbObject.put("longitude", longitude);
            trafficMeasurementDbObject.put("latitude", latitude);

            trafficMeasurements.insert(trafficMeasurementDbObject);

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildURL(String protocol, String host, String path, Map<String, String> URLParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol)
                .append(host)
                .append(path);

        for (Map.Entry<String, String> entry : URLParameters.entrySet())
        {
            sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }

        if (URLParameters.size() > 0) {
            int firstAmpersandIndex = sb.indexOf("&");
            sb.replace(firstAmpersandIndex, firstAmpersandIndex + 1, "?");
        }

        return sb.toString();
    }

    private JsonElement executeHttpRequest(String url, Map<String, String> headerParameters) throws IOException {
        logger.info("Execute request using" + url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        headerParameters.put("Accept", "application/json");
        headerParameters.entrySet().forEach(entry -> con.setRequestProperty(entry.getKey(), entry.getValue()));

        updateRequestsCounter(url);

        int responseCode = con.getResponseCode();
        logger.debug("\nSending 'GET' request to URL : " + con.getURL().toString());
        logger.debug("Response Code : " + responseCode);

        JsonElement element = new JsonObject();

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            element = gson.fromJson (reader, JsonElement.class);
            reader.close();
        }

        con.disconnect();
        logger.info("Request has been sent");
        return element;
    }

    private void updateRequestsCounter(String url) {
        if (url.contains(environment.getProperty("airly.host"))) {
            requestsCounter.incrementNumberOfAirlyRequests();
        }
        if (url.contains(environment.getProperty("here.host"))) {
            requestsCounter.incrementNumberOfHereRequests();
        }
        if (url.contains(environment.getProperty("open.weather.host"))) {
            requestsCounter.incrementNumberOfOpenWeatherRequests();
        }
    }
}
