package uk.gov.hmcts.reform.em.hrs.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountUrlHelperTest {
    @Test
    void testUrlWithSecondaryDNSWorks() {
        String input = "https://cvprecordingsstgsa-secondary.blob.core.windows.net/";
        String expected = "cvprecordingsstgsa";
        String actual = AccountUrlHelper.extractAccountFromUrl(input);
        assertEquals(expected,actual);
    }

    @Test
    void testUrlWithPrimaryDNSWorks() {
        String input = "https://cvprecordingsstgsa.blob.core.windows.net/";
        String expected = "cvprecordingsstgsa";
        String actual = AccountUrlHelper.extractAccountFromUrl(input);
        assertEquals(expected,actual);

    }

}
