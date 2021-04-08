package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.em.hrs.componenttests.config.TestApplicationConfig;
import uk.gov.hmcts.reform.em.hrs.util.Snooper;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@Disabled
@SpringBootTest(classes = {TestApplicationConfig.class, IngestionServiceImpl.class})
class IngestionServiceImplTest {
    @Inject
    private Snooper snooper;
    @Inject
    private IngestionServiceImpl underTest;

    @Captor
    private ArgumentCaptor<String> snoopCaptor;

    @BeforeEach
    void setup() {
        snoopCaptor.getAllValues().clear();
    }

    @Test
    void test1() {
        final Instant start = Instant.now(Clock.systemDefaultZone());

        underTest.ingest(null);

        final Instant end = Instant.now(Clock.systemDefaultZone());
        final Duration duration = Duration.between(start, end);

        assertThat(duration).hasSeconds(5);
        //        assertThat(messages).isEmpty();
        //        TimeUnit.SECONDS.sleep(1);
        //        assertThat(messages).singleElement().isEqualTo("Hello");
        //        TimeUnit.SECONDS.sleep(1);
        //        assertThat(messages).isNotEmpty().hasSameElementsAs(List.of("Hello", "Beautiful"));
        //        TimeUnit.SECONDS.sleep(1);

        verify(snooper, atLeastOnce()).snoop("Hello");
        verify(snooper, atLeastOnce()).snoop("Beautiful");
        verify(snooper, atLeastOnce()).snoop("World");
    }
}
