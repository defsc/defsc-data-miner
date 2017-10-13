package pl.edu.agh.analysis.pollution.utils;


import org.springframework.stereotype.Component;

@Component
public class RequestsCounter {
    private long airlyRequestsCounter;
    private long hereRequestsCounter;
    private long openWeatherRequestsCounter;

    public RequestsCounter() {}

    public void incrementNumberOfAirlyRequests() {
        airlyRequestsCounter++;
    }

    public void incrementNumberOfHereRequests() {
        hereRequestsCounter++;
    }

    public void incrementNumberOfOpenWeatherRequests() {
        openWeatherRequestsCounter++;
    }

    public long getAirlyRequestsCounter() {
        return airlyRequestsCounter;
    }

    public long getHereRequestsCounter() {
        return hereRequestsCounter;
    }

    public long getOpenWeatherRequestsCounter() {
        return openWeatherRequestsCounter;
    }

    @Override
    public String toString() {
        return "RequestsCounter{" +
                "airlyRequestsCounter=" + airlyRequestsCounter +
                ", hereRequestsCounter=" + hereRequestsCounter +
                ", openWeatherRequestsCounter=" + openWeatherRequestsCounter +
                '}';
    }
}
