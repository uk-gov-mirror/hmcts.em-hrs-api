package uk.gov.hmcts.reform.em.hrs.service.tokens;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.idam.client.IdamClient;

@Service
public class IdamHelper {

    private final IdamClient idamClient;

    private final String hrsSystemUserName;
    private final String hrsSystemUserPassword;

    public IdamHelper(IdamClient idamClient,
                      @Value("auth.idam.system-user.username") String username,
                      @Value("auth.idam.system-user.password") String password) {
        this.idamClient = idamClient;
        this.hrsSystemUserName = username;
        this.hrsSystemUserPassword = password;
    }

    public String getUserToken() {
        return idamClient.getAccessToken(hrsSystemUserName, hrsSystemUserPassword);
    }

    public String getUserId(String userAuthorization) {
        return idamClient.getUserDetails(userAuthorization).getId();
    }
}
