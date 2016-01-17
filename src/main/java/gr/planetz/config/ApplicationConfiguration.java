package gr.planetz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import gr.planetz.cache.CacheService;
import gr.planetz.cache.impl.PlayerCacheService;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CacheService cacheService(@Value("${player.cache.expiration.time.in.seconds}") final long durationInSeconds, @Value("${player.cache.cleanup.period.in.millis}") final long cleanUpPeriod) {
        return new PlayerCacheService(durationInSeconds, cleanUpPeriod);
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
