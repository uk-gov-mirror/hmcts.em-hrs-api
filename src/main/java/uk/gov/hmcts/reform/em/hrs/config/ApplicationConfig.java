package uk.gov.hmcts.reform.em.hrs.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;

@Configuration
public class ApplicationConfig {

    @Value("${notify.apiKey}")
    String notificationApiKey;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .build();
    }

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(notificationApiKey);
    }
}
