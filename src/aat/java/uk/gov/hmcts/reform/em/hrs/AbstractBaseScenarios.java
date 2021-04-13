package uk.gov.hmcts.reform.em.hrs;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.DatabaseTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;

@SpringBootTest(
    classes = {
        DatabaseTestConfig.class,
        EmTestConfig.class,
        CcdAuthTokenGeneratorConfiguration.class,
        ExtendedCcdHelper.class},
    properties = {"classpath:application.yml"}
)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AbstractBaseScenarios {
}
