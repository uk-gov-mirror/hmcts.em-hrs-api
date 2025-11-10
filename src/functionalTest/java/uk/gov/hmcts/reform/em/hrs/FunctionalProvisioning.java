package uk.gov.hmcts.reform.em.hrs;

import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.CcdAuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.hrs.testutil.ExtendedCcdHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamConfiguration;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Creates IDAM users required by functional tests.
 * Safe to run repeatedly (createUser should be idempotent).
 */
@SpringBootTest(classes = {
    ExtendedCcdHelper.class,
    CcdAuthTokenGeneratorConfiguration.class,
    AuthTokenGeneratorConfiguration.class,
    IdamConfiguration.class,
    EmTestConfig.class
})
@EnableAutoConfiguration
@ComponentScan(basePackages = {
    "uk.gov.hmcts.reform.em.test",
    "uk.gov.hmcts.reform.document"
})
@ExtendWith(SpringExtension.class)
@TestPropertySource(value = "classpath:application.yml")
@TestInstance(PER_CLASS)
public class FunctionalProvisioning extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalProvisioning.class);

    private static final String ROLE_CASE_WORKER = "caseworker";
    protected static final List<String> CASE_WORKER_ROLE = List.of(ROLE_CASE_WORKER);
    protected static final List<String> CASE_WORKER_HRS_SEARCHER_ROLE =
        List.of(ROLE_CASE_WORKER, "caseworker-hrs", "caseworker-hrs-searcher");
    protected static final List<String> CITIZEN_ROLE = List.of("citizen");

    @Autowired
    public FunctionalProvisioning(
        uk.gov.hmcts.reform.idam.client.IdamClient idamClient,
        uk.gov.hmcts.reform.em.test.idam.IdamHelper idamHelper,
        uk.gov.hmcts.reform.em.test.s2s.S2sHelper s2sHelper,
        uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi coreCaseDataApi,
        ExtendedCcdHelper extendedCcdHelper
    ) {
        super(idamClient, idamHelper, s2sHelper, coreCaseDataApi, extendedCcdHelper);
    }

    @Test
    public void provisionIdamUsers() {
        LOGGER.info("Provisioning test users for nightly functional tests");
        SerenityRest.useRelaxedHTTPSValidation();

        createIdamUser(SYSTEM_USER_FOR_FUNCTIONAL_TEST_ORCHESTRATION,
            List.of(ROLE_CASE_WORKER, "caseworker-hrs", "caseworker-hrs-searcher", "ccd-import", "caseworker-hrs-systemupdate"));

        createIdamUser(USER_WITH_SEARCHER_ROLE_CASEWORKER_HRS, CASE_WORKER_HRS_SEARCHER_ROLE);
        createIdamUser(USER_WITH_REQUESTOR_ROLE_CASEWORKER_ONLY, CASE_WORKER_ROLE);
        createIdamUser(USER_WITH_NONACCESS_ROLE_CITIZEN, CITIZEN_ROLE);
    }

    private void createIdamUser(String email, List<String> roles) {
        LOGGER.info("CREATING USER {} with roles {}", email, roles);
        this.idamHelper.createUser(email, roles);
    }
}
