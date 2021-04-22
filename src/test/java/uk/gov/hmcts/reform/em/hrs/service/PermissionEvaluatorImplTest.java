package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.em.hrs.repository.ShareesRepository;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.mockito.Mockito.when;

@TestPropertySource(properties = "hrs.allowed-roles.value=[caseworker-hrs]")
@RunWith(SpringJUnit4ClassRunner.class)
public class PermissionEvaluatorImplTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private ShareesRepository shareesRepository;

    private JwtAuthenticationToken authentication;

    private static final String USER_ID = UUID.randomUUID().toString();
    private static final UserInfo HRS_USER_INFO = UserInfo.builder()
        .uid(USER_ID)
        .roles(Arrays.asList("caseworker-hrs"))
        .build();
    private static final UserInfo NON_HRS_USER_INFO = UserInfo.builder()
        .uid(USER_ID)
        .roles(Arrays.asList("sharee"))
        .build();
    private UUID recordingId = UUID.randomUUID();
    private String shareeEmail = "sharee@sharee.com";

    @InjectMocks
    PermissionEvaluatorImpl permissionEvaluator;

    @Before
    public void setup() {
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "user")
            .build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read");
        authentication = new JwtAuthenticationToken(jwt, authorities);

    }

    @Test
    public void testPermissionOnDownloadCaseWorkerSuccess() {
        when(securityService.getUserInfo(Mockito.anyString())).thenReturn(HRS_USER_INFO);
        ReflectionTestUtils.setField(permissionEvaluator, "allowedRoles", Arrays.asList("caseworker-hrs"));

        Assert.assertTrue(permissionEvaluator.hasPermission(authentication, "", "READ"));
    }

    @Test
    public void testPermissionOnDownloadCaseWorkerFailure() {
        when(securityService.getUserInfo(Mockito.anyString())).thenReturn(HRS_USER_INFO);
        ReflectionTestUtils.setField(permissionEvaluator, "allowedRoles", Arrays.asList("caseworker"));

        Assert.assertFalse(permissionEvaluator.hasPermission(authentication, "", "READ"));
    }

    @Ignore
    @Test
    public void testPermissionOnDownloadShareeSuccess() {
        when(securityService.getUserInfo(Mockito.anyString())).thenReturn(NON_HRS_USER_INFO);
        ReflectionTestUtils.setField(permissionEvaluator, "allowedRoles", Arrays.asList("caseworker-hrs"));
        when(securityService.getUserEmail(Mockito.anyString())).thenReturn(shareeEmail);
        Assert.assertTrue(permissionEvaluator.hasPermission(authentication, "", "READ"));
    }
}
