package uk.gov.hmcts.reform.em.hrs.util;

import java.util.List;
import javax.inject.Named;

@Named
public class DefaultSnooper implements Snooper {
    @Override
    public void snoop(String message) {

    }

    @Override
    public List<String> getMessages() {
        return null;
    }

    @Override
    public void clearMessages() {

    }
}
