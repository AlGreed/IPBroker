package gr.planetz;

import org.apache.log4j.Logger;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMBeanExport
@EnableScheduling
@PropertySource("classpath:META-INF/settings/application.properties")
public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    public static void main(final String[] args) {
        @SuppressWarnings("resource")
        final ConfigurableApplicationContext run = new SpringApplicationBuilder().sources(Application.class).listeners(new ApplicationPidFileWriter()).run(args);
        LOG.info("STARTED");
    }
}
