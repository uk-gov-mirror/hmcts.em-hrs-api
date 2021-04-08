package uk.gov.hmcts.reform.em.hrs;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import uk.gov.hmcts.reform.em.hrs.testutil.TestAppConfig;

import java.time.Clock;
import java.time.LocalDateTime;

@SpringBootTest(classes = {TestAppConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = GetFileNamesScenarios.DatabaseDataSourceInitializer.class)
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
@Sql(scripts = "/db/populate-db.sql")
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

    public static class DatabaseDataSourceInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + "jdbc:postgresql://localhost:5444/emhrs",
                "spring.datasource.username=" + "emhrs",
                "spring.datasource.password=" + "emhrs"
            );
        }
    }
}
