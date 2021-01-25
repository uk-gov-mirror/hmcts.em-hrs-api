package uk.gov.hmcts.reform.em.hrs.controller;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.hrs.controllers.RootController;

import static org.assertj.core.api.Assertions.assertThat;

public class RootControllerTest {

    private final RootController rootController = new RootController();

    @Test
    public void test_should_return_welcome_response() {

        ResponseEntity<String> responseEntity = rootController.welcome();
        String expectedMessage = "Welcome to Hearing Recordings Service";

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expectedMessage);
    }
}
