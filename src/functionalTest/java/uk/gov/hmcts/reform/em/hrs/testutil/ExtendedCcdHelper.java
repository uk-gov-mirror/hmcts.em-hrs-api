package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefImportApi;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefUserRoleApi;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;

@Service
public class ExtendedCcdHelper {

    @Autowired
    private IdamHelper idamHelper;

    @Qualifier("ccdAuthTokenGenerator")
    @Autowired
    private AuthTokenGenerator ccdAuthTokenGenerator;

    @Autowired
    private CcdDefImportApi ccdDefImportApi;

    @Autowired
    private CcdDefUserRoleApi ccdDefUserRoleApi;

    @Value("${ccd-def.file}")
    protected String ccdDefinitionFile;


    public static String HRS_SYSTEM_API_USER = "em.hrs.api@hmcts.net.internal";
    public static List<String> HRS_SYSTEM_API_USER_ROLES = List.of("caseworker", "caseworker-hrs", "ccd-import");

    @PostConstruct
    public void init() throws Exception {
        importDefinitionFile();
    }

    public String getCcdS2sToken() {
        return ccdAuthTokenGenerator.generate();
    }

    private void importDefinitionFile() throws IOException {

         String ORIGINAL_CCD_UPLOADER_EMAIL = "hrs.test.user@hmcts.net";

//        String ccdAuthorisedUser = HRS_SYSTEM_API_USER; // this ought to be the account that creates the CCD def
        String ccdAuthorisedUser = ORIGINAL_CCD_UPLOADER_EMAIL;

        idamHelper.createUser(HRS_SYSTEM_API_USER, HRS_SYSTEM_API_USER_ROLES);
        createCcdUserRole("caseworker");
        createCcdUserRole("caseworker-hrs");


        MultipartFile multipartFile = new MockMultipartFile(
            "x",
            "x",
            "application/octet-stream",
            getHrsDefinitionFile());

        ccdDefImportApi.importCaseDefinition(idamHelper.authenticateUser(HRS_SYSTEM_API_USER),
                                             ccdAuthTokenGenerator.generate(), multipartFile);
    }

    private InputStream getHrsDefinitionFile() {
        return ClassLoader.getSystemResourceAsStream(ccdDefinitionFile);
    }

    private void createCcdUserRole(String userRole) {
        ccdDefUserRoleApi.createUserRole(new CcdDefUserRoleApi.CreateUserRoleBody(userRole, "PUBLIC"),
                                         idamHelper.authenticateUser(HRS_SYSTEM_API_USER), ccdAuthTokenGenerator.generate());
    }
}
