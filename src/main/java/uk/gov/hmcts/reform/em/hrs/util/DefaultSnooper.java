package uk.gov.hmcts.reform.em.hrs.util;


//import lombok.extern.slf4j.Slf4j;


import org.apache.logging.log4j.Logger;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Named
//@Slf4j
public class DefaultSnooper implements Snooper {

private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnooper.class);

    @Override
    public void snoop(String message) {
        // TODO: covered by EM-3582
//        log.info(message);
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
