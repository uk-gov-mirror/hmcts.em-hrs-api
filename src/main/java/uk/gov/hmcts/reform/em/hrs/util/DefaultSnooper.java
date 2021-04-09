package uk.gov.hmcts.reform.em.hrs.util;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;

@Named
@Slf4j
public class DefaultSnooper implements Snooper {

    @Override
    public void snoop(final String message) {
        // TODO: covered by EM-3582
        log.info(message);
    }

    @Override
    public void snoop(final String message, final Throwable throwable) {
        // TODO: covered by EM-3582
        log.error(message, throwable);
    }
}
