package uk.gov.hmcts.reform.em.hrs.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.hrs.controller.HearingRecordingController;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.ScheduledTaskRunner;
import uk.gov.hmcts.reform.em.hrs.service.SegmentDownloadService;
import uk.gov.hmcts.reform.em.hrs.service.ShareAndNotifyService;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.doNothing;

@ActiveProfiles("contract")
@Provider("em_hrs_hearing_recording_api")
@PactBroker(
    scheme = "${PACT_BROKER_SCHEME:http}",
    host = "${PACT_BROKER_URL:localhost}",
    port = "${PACT_BROKER_PORT:80}",
    consumerVersionSelectors = {@VersionSelector(tag = "master")}
)
//@PactFolder("pacts")
@IgnoreNoPactsToVerify
@ExtendWith(SpringExtension.class)
@WebMvcTest(
    value = { HearingRecordingController.class, ScheduledTaskRunner.class},
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
public class HearingRecordingProviderTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HearingRecordingService hearingRecordingService;

    @MockitoBean
    private ShareAndNotifyService shareAndNotifyService;

    @MockitoBean
    private SegmentDownloadService segmentDownloadService;

    @MockitoBean(name = "ingestionQueue")
    private LinkedBlockingQueue<HearingRecordingDto> ingestionQueue;

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

    @State("Hearing recordings exist for given CCD case IDs to delete")
    public void setupHearingRecordingsExist() {
        // Mock the service to do nothing on delete
        doNothing().when(hearingRecordingService).deleteCaseHearingRecordings(List.of(123456789L, 987654321L));
    }
}
