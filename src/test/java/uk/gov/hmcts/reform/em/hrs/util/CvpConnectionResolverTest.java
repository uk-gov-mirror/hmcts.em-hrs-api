package uk.gov.hmcts.reform.em.hrs.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CvpConnectionResolverTest {
    @Test
    public void testIsACvpEndpointUrl() {
        assertFalse(CvpConnectionResolver.isACvpEndpointUrl("https://example.org/example"));
        assertTrue(CvpConnectionResolver.isACvpEndpointUrl("cvprecordings"));
    }
}

