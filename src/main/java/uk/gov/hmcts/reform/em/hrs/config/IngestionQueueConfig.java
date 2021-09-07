package uk.gov.hmcts.reform.em.hrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.em.hrs.util.IngestionQueue;

@Configuration
public class IngestionQueueConfig {

    @Value("${hrs.ingestion-queue-size}")
    private Integer ingestionQueueSize;

    @Bean
    public IngestionQueue provideIngestionQueue() {
        return IngestionQueue.builder()
            .capacity(ingestionQueueSize)
            .build();
    }


}
