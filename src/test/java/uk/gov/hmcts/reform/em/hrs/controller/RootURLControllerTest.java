package uk.gov.hmcts.reform.em.hrs.controller;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.hrs.controllers.RootURLController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RootURLControllerTest {

    private final RootURLController rootURLController = new RootURLController();

    @Test
    public void test_should_return_welcome_response() {

        ResponseEntity<String> responseEntity = rootURLController.welcome();
        String expectedMessage = "Welcome to Hearing Recordings Service";

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expectedMessage);
    }
}
