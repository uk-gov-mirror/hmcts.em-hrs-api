package uk.gov.hmcts.reform.em.hrs.smoke.util;

import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class TestUtil {

    @Inject
    private IdamHelper idamHelper;

    @Inject
    private S2sHelper s2sHelper;

    private String idamAuth;
    private String s2sAuth;

    private UUID documentId = UUID.randomUUID();

    @PostConstruct
    void postConstruct() {
        RestAssured.useRelaxedHTTPSValidation();
        idamHelper.createUser("a@b.com", Stream.of("caseworker").collect(Collectors.toList()));
        idamAuth = idamHelper.authenticateUser("a@b.com");
        s2sAuth = s2sHelper.getS2sToken();
    }

    public RequestSpecification authRequest() {
        return RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", s2sHelper.getS2sToken());
    }
}
