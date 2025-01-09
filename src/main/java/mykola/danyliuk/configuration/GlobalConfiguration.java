package mykola.danyliuk.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableCaching
@EnableScheduling
@Configuration
public class GlobalConfiguration {

    public static final String API_ENDPOINT = "/api/v1/";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
