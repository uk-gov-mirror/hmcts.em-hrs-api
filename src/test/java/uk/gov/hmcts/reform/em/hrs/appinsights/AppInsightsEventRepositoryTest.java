package uk.gov.hmcts.reform.em.hrs.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@Disabled
@SpringBootTest(
    classes = {TelemetryClient.class, AppInsightsEventRepository.class},
    properties = {"azure.application-insights.instrumentation-key=SomeHrsKey"}
)
class AppInsightsEventRepositoryTest {
    @MockBean
    private TelemetryClient telemetryClient;

    @InjectMocks
    private AppInsightsEventRepository underTest;

    @Test
    void testTrackEvent() {
        doNothing().when(telemetryClient).trackEvent(anyString(), anyMap(), any());

        underTest.trackEvent("SomeName", Collections.emptyMap());

        verify(telemetryClient).trackEvent(anyString(), anyMap(), any());
    }
}
