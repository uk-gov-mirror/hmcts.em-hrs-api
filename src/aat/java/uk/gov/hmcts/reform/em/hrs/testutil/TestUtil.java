package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {



    @Value("${test.url}")
    private String testUrl;

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

}
