package uk.gov.hmcts.reform.em.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);

    private final IdamClient idamClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final String systemUsername;
    private final String systemUserPassword;

    @Value("${idam.client.id:}") String clientId;
    @Value("${idam.client.secret:}") String clientSecret;

    @Inject
    public SecurityServiceImpl(final IdamClient idamClient,
                               final AuthTokenGenerator authTokenGenerator,
                               final @Value("${idam.system-user.username}") String systemUsername,
                               final @Value("${idam.system-user.password}") String systemUserPassword) {
        this.idamClient = idamClient;
        this.authTokenGenerator = authTokenGenerator;
        this.systemUsername = systemUsername;
        this.systemUserPassword = systemUserPassword;
    }

    @Override
    public Map<String, String> getTokens() {
        final String token = getUserToken();
        return Map.of("user", token,
                      "userId", getUserId(token),
                      "service", authTokenGenerator.generate());
    }

    @Override
    public String getUserToken() {
        LOGGER.info("getting the user token with client-id ({}) and client-secret ({})", clientId, clientSecret);
        return idamClient.getAccessToken(systemUsername, systemUserPassword);
    }

    @Override
    public String getUserId() {
        return getUserId(getUserToken());
    }

    @Override
    public String getUserId(String userAuthorization) {
        return idamClient.getUserDetails(userAuthorization).getId();
    }

    @Override
    public String getUserEmail() {
        return getUserEmail(getUserToken());
    }

    @Override
    public String getUserEmail(String userAuthorization) {
        return idamClient.getUserDetails(userAuthorization).getEmail();
    }
}
