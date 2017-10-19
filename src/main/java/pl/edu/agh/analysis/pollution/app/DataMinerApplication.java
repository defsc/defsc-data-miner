package pl.edu.agh.analysis.pollution.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataMinerApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger("pl.edu.agh.analysis.pollution.app.DataMinerApplication");

    public static void main(String[] args) {
        LOGGER.info("Starting application with arguments %s", args);
        SpringApplication app = new SpringApplication(DataMinerApplication.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
    }

}
