package uk.gov.hmcts.reform.em.hrs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.repository.JobInProgressRepository;
import uk.gov.hmcts.reform.em.hrs.testutil.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import java.time.Clock;
import java.time.LocalDateTime;
import javax.inject.Inject;

@SpringBootTest(classes = {EmTestConfig.class, AuthTokenGeneratorConfiguration.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
public class GetFileNamesScenarios {

    /*@Inject
    private JobInProgressRepository jobInProgressRepository;

    @Rule
    public RetryRule retryRule = new RetryRule(1);

    @Value("${test.url}")
    private String testUrl;*/

    @Test
    public void testShouldDeleteJobsOlderThanTwentyFourHours() {
        final LocalDateTime twentyFourHoursAgo = LocalDateTime.now(Clock.systemUTC()).minusHours(24);
        //jobInProgressRepository.deleteByCreatedOnLessThan(twentyFourHoursAgo);
    }

}
