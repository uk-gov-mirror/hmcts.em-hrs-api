package uk.gov.hmcts.reform.em.hrs.componenttests.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.hmcts.reform.em.hrs.util.IngestionQueue;
import uk.gov.hmcts.reform.em.hrs.util.Snooper;

@TestConfiguration
public class TestApplicationConfig {
    @Bean
    @Primary
    public Snooper provideSnooper() {
        return Mockito.spy(Snooper.class);
    }

    @Bean
    @Primary
    public IngestionQueue provideIngestionQueue() {
        return IngestionQueue.INSTANCE;
    }
}
