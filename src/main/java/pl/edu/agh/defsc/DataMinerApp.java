package pl.edu.agh.defsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan("pl.edu.agh.defsc")
@SpringBootApplication
public class DataMinerApp {
    private final static Logger LOGGER = LoggerFactory.getLogger("app.DataMinerApplication");

    public static void main(String[] args) {
        LOGGER.info("Starting application with arguments %s", args);
        SpringApplication app = new SpringApplication(DataMinerApp.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
    }

}