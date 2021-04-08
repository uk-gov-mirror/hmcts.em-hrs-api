package uk.gov.hmcts.reform.em.hrs;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, CcdAuthTokenGeneratorConfiguration.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseUpdateScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Rule
    public RetryRule retryRule = new RetryRule(1);

    @Value("${test.url}")
    private String testUrl;

    @Test
    public void testCaseCreation() {
        HearingRecordingDto request = extendedCcdHelper.createRecordingSegment(
            "http://dm-store-aat.service.core-compute-aat.internal:80/documents/e486435e-30e8-456c-9d4d-4adffcb50010",//TODO: need to ask CCD not to reject HRS URLs
            "hearing-recording-segment",
            ".mp4",
            12L,
            0
            );

        Response response = SerenityRest
            .given()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(request)
            .post("/segments");

        Assert.assertEquals(202, response.getStatusCode());
    }

    @Test
    public void testCaseUpdate() {
        HearingRecordingDto request = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/57340931-bfce-424d-bcee-dd44ee55ff66",
            "hearing-recording-segment",
            ".mp4",
            12L,
            1
            );

        Response response = SerenityRest
            .given()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .body(request)
            .post("/segments");

        Assert.assertEquals(202, response.getStatusCode());
    }
}
