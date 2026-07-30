package br.com.qawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QawlerApplication.class, args);
    }
}
