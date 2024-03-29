package eu.kaesebrot.dev.transcodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


@Configuration
@SpringBootApplication
public class TranscodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranscodeServiceApplication.class, args);
    }
}