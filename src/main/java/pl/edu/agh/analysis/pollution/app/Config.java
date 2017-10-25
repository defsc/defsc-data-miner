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
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

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
        logger.info("Starting tasks scheduled for once per day");
        updateSensorList();
        updateAirPollutionMeasurements();
        logger.info("Finished tasks scheduled for once per day");
    }

    @Scheduled(fixedRate=3600000)
    public void oncePerHour() throws IOException {
        logger.info("Starting tasks scheduled for once per hour");
        Instant now = Instant.now();
        updateWeatherMeasurements(now);
        updateTrafficFlowMeasurements(now);
        logger.info(requestsCounter.toString());
        logger.info("Finished tasks scheduled for once per hour");
    }

    private void updateSensorList() throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));

        logger.info("Updating Sensors list");

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

        logger.debug(String.format("Sending sensor request to URL %s with header %s", url, headerParameters));
        JsonElement response = executeHttpRequest(url, headerParameters);
        JsonArray results = response.getAsJsonArray();

        for (JsonElement result  : results) {
            DBObject dbObject = (DBObject) JSON.parse(result.toString());

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("id", dbObject.get("id"));
            DBCursor cursor = sensors.find(whereQuery);

            if (wasFoundFirstTime(cursor)) {
                logger.info(String.format("Inserting new sensor into database of id %d", dbObject.get("id")));
                sensors.insert(dbObject);
                logger.debug(String.format("New sensor %s for %s", dbObject.get("id"), dbObject.toString()));
            }
        }
    }

    private boolean wasFoundFirstTime(DBCursor cursor) {
        return cursor.size() == 0;
    }

    private void updateAirPollutionMeasurements() throws IOException {
        DBCollection sensors = mongoTemplate.getCollection(environment.getProperty("air.pollution.sensors.collection.name"));
        DBCollection measurements = mongoTemplate.getCollection(environment.getProperty("air.pullution.measurements.collection.name"));
        DBCursor cursor = sensors.find();

        logger.info(String.format("Updating air pollution measurement for %d sensors", cursor.size()));

        while(cursor.hasNext()) {
            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(String.format("Getting history of measurements for sensor %s (%s:%s)",id, longitude, latitude));

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

            logger.debug(String.format("Sending air pollution measurement request to URL %s with header %s", url, headerParameters));
            JsonElement element = executeHttpRequest(url, headerParameters);
            JsonArray results = element.getAsJsonObject().getAsJsonArray("history");
            logger.info(String.format("Measurements for last %d hours have been received from sensor %s", results.size(), id));

            int fullMeasures = 0;
            int measures = 0;

            for (JsonElement result  : results) {
                DBObject dbObject = (DBObject) JSON.parse(result.toString());
                dbObject.put("sensor_id", id);
                dbObject.put("longitude", longitude);
                dbObject.put("latitude", latitude);
                measurements.insert(dbObject);
                logger.debug("Inserted data from sensor %s: %s", id, dbObject.toString());

                if(!dbObject.get("measurements").toString().equals("{ }")) {
                    fullMeasures++;
                }
                measures++;
            }

            logger.info(String.format("%d measures with %d not-empty values was inserted into database", measures, fullMeasures));

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

        logger.info("Updating weather measurements");

        while(cursor.hasNext()) {

            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(String.format("Getting weather data for sensor %s (%s:%s)",id, longitude, latitude));

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

            logger.debug(String.format("Sending weather measurement request to URL %s with header %s", url, headerParameters));

            JsonElement element = executeHttpRequest(url, headerParameters);
            DBObject weatherMeasurementDbObject = (DBObject) JSON.parse(element.toString());

            weatherMeasurementDbObject.put("timestamp", now.toString());
            weatherMeasurementDbObject.put("longitude", longitude);
            weatherMeasurementDbObject.put("latitude", latitude);

            logger.info("Inserting weather data");
            logger.debug(String.format("Inserted weather data is %s", weatherMeasurementDbObject.toString()));
            weatherMeasurements.insert(weatherMeasurementDbObject);
            logger.info(String.format("Inserted weather data from sensor %s:%s", id, weatherMeasurementDbObject.toString()));

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

        logger.info("Updating traffic measurements");

        while(cursor.hasNext()) {
            BasicDBObject object= (BasicDBObject) cursor.next();

            String id = object.getString("id");
            String longitude = ((BasicDBObject)object.get("location")).getString("longitude");
            String latitude = ((BasicDBObject)object.get("location")).getString("latitude");

            logger.info(String.format("Getting traffic data for sensor %s (%s:%s)",id, longitude, latitude));

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

            logger.debug(String.format("Sending traffic measurement request to URL %s with header %s", url, headerParameters));
            JsonElement element = executeHttpRequest(url, headerParameters);
            DBObject trafficMeasurementDbObject = (DBObject) JSON.parse(element.toString());

            trafficMeasurementDbObject.put("timestamp", now.toString());
            trafficMeasurementDbObject.put("longitude", longitude);
            trafficMeasurementDbObject.put("latitude", latitude);

            logger.info("Inserting traffic data");
            logger.debug(String.format("Inserted traffic data is %s", trafficMeasurementDbObject.toString()));
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
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        headerParameters.put("Accept", "application/json");
        headerParameters.entrySet().forEach(entry -> con.setRequestProperty(entry.getKey(), entry.getValue()));

        updateRequestsCounter(url);

        logger.debug("Sending 'GET' request to URL : " + con.getURL().toString());
        logger.debug("Connection info:" + "header:"+con.getHeaderFields().keySet().toString() + "|cipher" + ((HttpsURLConnectionImpl) con).getCipherSuite().toString() + "|" + "resp:" + con.getResponseMessage());
        int responseCode = con.getResponseCode();

        JsonElement element = new JsonObject();

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            element = gson.fromJson (reader, JsonElement.class);
            reader.close();
        }

        con.disconnect();
        logger.debug("url:" + url + "|resp:" + element + "|respCode:" + responseCode );
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
