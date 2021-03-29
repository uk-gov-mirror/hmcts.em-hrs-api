package uk.gov.hmcts.reform.em.hrs;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {EmTestConfig.class, AuthTokenGeneratorConfiguration.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseUpdateScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Rule
    public RetryRule retryRule = new RetryRule(1);

    @Test
    public void testCaseCreation() {
        HearingRecordingDto request = extendedCcdHelper.createRecordingSegment(
            "http://em-hrs-api:8080/documents/57340931-bfce-424d-bcee-6b3f0abf56fb",
            "first-hearing-recording-segment",
            ".mp4",
            12,
            0
            );

        Response response = SerenityRest
            .given()
            .baseUri("http://localhost:8080")
            .contentType(APPLICATION_JSON_VALUE)
            .body(request)
            .post("/folders/london/hearing-recordings/first-hearing-recording-segment/segments");


        Assert.assertEquals(202, response.getStatusCode());
    }

    @Test
    public void testCaseUpdate() {
        HearingRecordingDto request = extendedCcdHelper.createRecordingSegment(
            "http://em-hrs-api:8080/documents/57340931-bfce-424d-bcee-6b3f0abf56fb",
            "first-hearing-recording-segment",
            ".mp4",
            12,
            0
            );

        Response response = SerenityRest
            .given()
            .baseUri("http://localhost:8080")
            .contentType(APPLICATION_JSON_VALUE)
            .body(request)
            .post("/folders/london/hearing-recordings/first-hearing-recording-segment/segments");


        Assert.assertEquals(202, response.getStatusCode());
    }
}
