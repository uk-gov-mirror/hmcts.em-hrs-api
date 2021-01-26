package uk.gov.hmcts.reform.em.hrs.smoke;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
//import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.hrs.testutil.TestUtil;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestUtil.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Smoke")})
public class SmokeTest {

    private static final String MESSAGE = "Welcome to Hearing Recordings Service";

    @Autowired
    private TestUtil testUtil;

    @Test
    public void testHealthEndpoint() {

        SerenityRest.useRelaxedHTTPSValidation();

        String response =
                SerenityRest
                        .given()
                        .baseUri(testUtil.getTestUrl())
                        .when()
                        .get("/")
                        .then()
                        .statusCode(200).extract().body().asString();


        assertEquals(MESSAGE, response);
    }
}
