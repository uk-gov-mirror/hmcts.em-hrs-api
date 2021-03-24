package uk.gov.hmcts.reform.em.hrs.controller;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.em.hrs.config.security.JwtGrantedAuthoritiesConverter;

import static org.mockito.Mockito.doReturn;

public abstract class BaseTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    protected MockMvc mockMvc;

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;

    @Before
    public void setupMocks() {

        MockitoAnnotations.initMocks(this);

        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
