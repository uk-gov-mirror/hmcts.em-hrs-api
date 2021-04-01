package uk.gov.hmcts.reform.em.hrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;

@Configuration
public class ApplicationConfig {

    @Value("${notify.apiKey}")
    String notificationApiKey;

    @Bean
    public NotificationClientApi provideNotificationClient() {
        return new NotificationClient(notificationApiKey);
    }
}
