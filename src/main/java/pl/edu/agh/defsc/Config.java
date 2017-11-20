package pl.edu.agh.defsc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import jdk.incubator.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.defsc.deserializer.AirlyLocalizationOfMeasurementsDeserializer;
import pl.edu.agh.defsc.entity.localizations.AirlyLocalizationOfMeasurements;

@Configuration
public class Config {

    @Bean
    public JsonParser jsonParser() {
        return new JsonParser();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().build();
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AirlyLocalizationOfMeasurements.class,
                new AirlyLocalizationOfMeasurementsDeserializer());

        return gsonBuilder.create();
    }

}
