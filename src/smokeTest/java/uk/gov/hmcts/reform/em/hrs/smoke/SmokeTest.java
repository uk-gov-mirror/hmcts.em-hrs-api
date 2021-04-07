package uk.gov.hmcts.reform.em.hrs.smoke;

import io.restassured.RestAssured;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.smoke.config.AuthTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = {AuthTokenGeneratorConfiguration.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@WithTags({@WithTag("testType:Smoke")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

    private static final String MESSAGE = "Welcome to Hearing Recordings Service";

    @Value("${test.url}")
    private String testUrl;

    @Inject
    AuthTokenGenerator authTokenGenerator;

    @Inject
    private IdamHelper idamHelper;

    @BeforeAll
    public void initialise() {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void testHealthEndpoint() {

        String response =
            RestAssured
                .given()
                .baseUri(testUrl)
                .when()
                .get("/")
                .then()
                .statusCode(200).extract().body().asString();
        assertEquals(MESSAGE, response);
    }

    @Test
    public void testEndpointUpAnRunning() {

        RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", authTokenGenerator.generate())
            .baseUri(testUrl)
            .when()
            .get("/folders/testPath")
            .then()
            .statusCode(200).extract().body().asString();
    }

}
