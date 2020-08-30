package tfg.backend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@org.springframework.context.annotation.Configuration
public class Configuration {

    /*
     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
     */
    public @Bean
    MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017/backend");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/serie/**").allowedOrigins("http://localhost:5000");
            }
        };
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/msg");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
