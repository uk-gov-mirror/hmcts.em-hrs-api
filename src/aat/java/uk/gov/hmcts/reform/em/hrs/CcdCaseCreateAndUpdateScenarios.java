package uk.gov.hmcts.reform.em.hrs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

@SpringBootTest(classes = {EmTestConfig.class, ExtendedCcdHelper.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CcdCaseCreateAndUpdateScenarios {

    @Autowired
    protected ExtendedCcdHelper extendedCcdHelper;

    @Rule
    public RetryRule retryRule = new RetryRule(1);

    @Test
    public void testHRSCaseCreation() {
        HearingRecordingDto hearingRecordingDto = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/57340931-bfce-424d-bcee-6b3f0abf56fb",
            "first-hearing-recording-segment",
            12,
            0
            );

        CaseDetails caseDetails = extendedCcdHelper.createHRSCase(hearingRecordingDto);
        System.out.println("these are the case details " + caseDetails);
    }
    @Test
    public void testUnHRSCaseUpdate() {
        HearingRecordingDto hearingRecordingDto = extendedCcdHelper.createRecordingSegment(
            "http://dm-store:8080/documents/57340931-bfce-424d-bcee-123456789012",
            "second-hearing-recording-segment",
            25,
            1
        );

        CaseDetails caseDetails = extendedCcdHelper.updateHRSCase(hearingRecordingDto, "PUT CASE-ID HERE");
        System.out.println("these are the case details " + caseDetails);
    }
}
