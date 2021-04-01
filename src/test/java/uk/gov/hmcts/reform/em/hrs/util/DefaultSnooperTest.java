package uk.gov.hmcts.reform.em.hrs.util;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultSnooperTest {

    @Test
    void testsnoop(){
        DefaultSnooper manager = Mockito.mock(DefaultSnooper.class);
        Logger logger = Mockito.mock(DefaultSnooper.LOGGER);
        String expectedMessage = "";
        manager.snoop(expectedMessage);
        logger.info(expectedMessage);

        Mockito.verify(manager).snoop(expectedMessage);
    }

    @Test
    void testGetMessages(){
        List<String> expectedMessages = new ArrayList<String>();
        DefaultSnooper DS = new DefaultSnooper();
        List<String> actualMessages = DS.getMessages();
        assertThat(expectedMessages).hasSameElementsAs(actualMessages);
    }

    @Test
    void testClearMessages(){
        DefaultSnooper manager = Mockito.mock(DefaultSnooper.class);
        manager.clearMessages();

        Mockito.verify(manager).clearMessages();
    }

}
