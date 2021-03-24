package uk.gov.hmcts.reform.em.hrs.helper;

import uk.gov.hmcts.reform.em.hrs.util.Snooper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Named;

@Named
public class SimpleSnooper implements Snooper {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void snoop(final String message) {
        messages.add(message);
    }

    @Override
    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public void clearMessages() {
        messages.clear();
    }
}
