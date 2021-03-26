package uk.gov.hmcts.reform.em.hrs.util;

import java.util.List;

public interface Snooper {
    void snoop(String message);

    List<String> getMessages();

    void clearMessages();
}
