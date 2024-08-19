package com.paw.project;

import com.paw.project.Utils.Repositories.PlansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class Application {
    @Autowired
    private  PlansRepository plansRepository;

    public Application(PlansRepository plansRepository) {
        this.plansRepository = plansRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    /* Added by Larisa just for test Angular. It needs data to work with */
    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200").maxAge(30000);
            }
        };
    }
}
