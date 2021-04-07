package uk.gov.hmcts.reform.em.hrs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DefaultSnooperTest {

    private final DefaultSnooper defaultSnooper = new DefaultSnooper();

    @Test
    void test_snoop() {
        defaultSnooper.snoop("Test message");
    }

    @Test
    void test_get_messages() {
        assertEquals(0, defaultSnooper.getMessages().size());
    }

    @Test
    void test_clear_messages() {
        defaultSnooper.clearMessages();
    }

}
