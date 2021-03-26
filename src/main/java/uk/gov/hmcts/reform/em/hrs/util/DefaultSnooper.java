package uk.gov.hmcts.reform.em.hrs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import javax.inject.Named;

@Named
public class DefaultSnooper implements Snooper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnooper.class);

    @Override
    public void snoop(String message) {
        // TODO: covered by EM-3582
        LOGGER.info(message);
    }

    @Override
    public List<String> getMessages() {
        // TODO: covered by EM-3582
        return Collections.emptyList();
    }

    @Override
    public void clearMessages() {
        // TODO: covered by EM-3582
    }
}
