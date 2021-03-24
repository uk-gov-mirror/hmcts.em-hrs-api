package uk.gov.hmcts.reform.em.hrs;

import net.serenitybdd.rest.SerenityRest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseUpdateScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Rule
    public RetryRule retryRule = new RetryRule(1);

    @Test
    public void testHrsCaseCreation() {
        HearingRecordingDto request = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/57340931-bfce-424d-bcee-6b3f0abf56fb",
            "first-hearing-recording-segment",
            12,
            0
            );

        SerenityRest
            .given()
            .baseUri("http://localhost:8080")
            .contentType(APPLICATION_JSON_VALUE)
            .body(request)
            .post("/folders/london/hearing-recording");

        System.out.println("these are the case details ");
    }

    @Test
    public void testUnHrsCaseUpdate() {
        HearingRecordingDto hearingRecordingDto = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/57340931-bfce-424d-bcee-123456789012",
            "second-hearing-recording-segment",
            25,
            1
        );
        System.out.println("these are the case details ");
    }
}
