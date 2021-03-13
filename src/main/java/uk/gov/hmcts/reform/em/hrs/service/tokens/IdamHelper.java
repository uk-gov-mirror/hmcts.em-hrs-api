package uk.gov.hmcts.reform.em.hrs.service.tokens;

public class IdamHelper {

    private String SYSTEM_USER_NAME;

    public String getUserToken() {
        return "userToken";//TODO - Call IDAM/getUserDetails to get userToken
    }

    public String getUserId() {
        return "USER_ID";//TODO -  GET ACTUAL SYSTEM USER ID FROM IDAM
    }
}
