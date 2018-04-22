package pm.ftm.tonto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class TontoApplication {
    /**
     * Main
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TontoApplication.class, args);
    }
}
