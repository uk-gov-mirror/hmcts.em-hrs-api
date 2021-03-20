package uk.gov.hmcts.reform.em.hrs.service.tokens;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecurityClient {

    private final IdamClient idamClient;
    private final AuthTokenGenerator authTokenGenerator;

    private final String hrsSystemUserName;
    private final String hrsSystemUserPassword;

    public SecurityClient(IdamClient idamClient,
                          AuthTokenGenerator authTokenGenerator,
                          @Value("auth.idam.system-user.username") String username,
                          @Value("auth.idam.system-user.password") String password) {
        this.idamClient = idamClient;
        this.authTokenGenerator = authTokenGenerator;
        this.hrsSystemUserName = username;
        this.hrsSystemUserPassword = password;
    }

    public Map<String, String> getTokens() {
        Map<String, String> securityTokens = new HashMap<>();
        String token = getUserToken();
        securityTokens.put("user", token);
        securityTokens.put("userId", getUserId(token));
        securityTokens.put("service", authTokenGenerator.generate());
        return securityTokens;
    }

    public String getUserToken() {
        return idamClient.getAccessToken(hrsSystemUserName, hrsSystemUserPassword);
    }

    public String getUserId(String userAuthorization) {
        return idamClient.getUserDetails(userAuthorization).getId();
    }
}
