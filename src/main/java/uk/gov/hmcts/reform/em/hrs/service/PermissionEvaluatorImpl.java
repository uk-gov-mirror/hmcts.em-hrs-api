package uk.gov.hmcts.reform.em.hrs.service;


import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionEvaluatorImpl.class);

    @Value("#{'${hrs.allowed-roles}'.split(',')}")
    private List<String> allowedRoles;

    @Autowired
    private SecurityService securityService;


    @Override
    public boolean hasPermission(@NotNull Authentication authentication,
                                 @NotNull Object targetDomainObject,
                                 @NotNull Object permissionString) {

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = "Bearer " + jwtAuthenticationToken.getToken().getTokenValue();

        UserInfo userInfo = securityService.getUserInfo(token);

        if (CollectionUtils.isNotEmpty(userInfo.getRoles())) {
            Optional<String> userRole = userInfo.getRoles().stream()
                                            .filter(role -> allowedRoles.contains(role))
                                            .findFirst();
            if (userRole.isPresent()) {
                return true;
            }
        }
        String emailId = securityService.getUserEmail(token);

        return false;
    }

    @Override
    public boolean hasPermission(@NotNull Authentication authentication,
                                 @NotNull Serializable serializable,
                                 @NotNull String className,
                                 @NotNull Object permissions) {
        LOGGER.error("Should not be invoking this method");
        return false;
    }
}
