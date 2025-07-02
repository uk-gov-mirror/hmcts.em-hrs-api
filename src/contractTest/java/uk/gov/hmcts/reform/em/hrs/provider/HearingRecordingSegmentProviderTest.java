package uk.gov.hmcts.reform.em.hrs.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors;
import au.com.dius.pact.provider.junitsupport.loader.SelectorBuilder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.hrs.config.WebConfig;
import uk.gov.hmcts.reform.em.hrs.controller.HearingRecordingController;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.ScheduledTaskRunner;
import uk.gov.hmcts.reform.em.hrs.service.SegmentDownloadService;
import uk.gov.hmcts.reform.em.hrs.service.ShareAndNotifyService;

import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.when;

@Provider("em_hrs_api_recording_segments_provider")
@PactBroker(
    url = "${PACT_BROKER_FULL_URL:http://localhost:80}",
    providerBranch = "${pact.provider.branch}"
)
//@PactFolder("pacts")
@IgnoreNoPactsToVerify
@ExtendWith(SpringExtension.class)
@WebMvcTest(
    value = {HearingRecordingController.class},
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
public class HearingRecordingSegmentProviderTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduledTaskRunner scheduledTaskRunner;
    @MockitoBean
    private HearingRecordingService hearingRecordingService;
    @MockitoBean
    private ShareAndNotifyService shareAndNotifyService;
    @MockitoBean
    private SegmentDownloadService segmentDownloadService;
    @MockitoBean(name = "ingestionQueue")
    private LinkedBlockingQueue<HearingRecordingDto> ingestionQueue;

    @PactBrokerConsumerVersionSelectors
    public static SelectorBuilder consumerVersionSelectors() {
        return new SelectorBuilder()
            .matchingBranch()
            .mainBranch()
            .deployedOrReleased();
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        System.getProperties().setProperty("pact.verifier.publishResults", "true");
        if (context != null) {
            context.setTarget(new MockMvcTestTarget(mockMvc));
        }
    }


    @State("Ready to accept a new hearing recording segment")
    public void setupSegmentPost() {
        // Simulate that the ingestion queue accepts the segment
        when(ingestionQueue.offer(org.mockito.ArgumentMatchers.any(HearingRecordingDto.class))).thenReturn(true);
    }
}
