package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefImportApi;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefUserRoleApi;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

@Service
public class ExtendedCcdHelper {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private CcdDefImportApi ccdDefImportApi;

    @Autowired
    private CcdDefUserRoleApi ccdDefUserRoleApi;

    private String hrsTester = "hrs.test.user@hmcts.net";
    private List<String> hrTesterRoles = Arrays.asList("caseworker", "caseworker-hrs", "ccd-import");

    @PostConstruct
    public void init() throws Exception {
        initHrsTestUser();
        importDefinitionFile();
    }

    public HearingRecordingDto createRecordingSegment(
        String url, String filename, String fileExt, int length, int segment) {
        return new HearingRecordingDto(
            "hearing-12-family-probate-morning",
            "CVP",
            "London",
            "PROBATE",
            "HRS",
            "12",
            "hearing-12-family-probate-morning",
            url,
            filename,
            fileExt,
            length,
            segment,
            "",
            LocalDateTime.now()
        );
    }

    private void importDefinitionFile() throws IOException {

        createUserRole("caseworker");
        createUserRole("caseworker-hrs");

        MultipartFile multipartFile = new MockMultipartFile(
            "x",
            "x",
            "application/octet-stream",
            getHrsDefinitionFile());

        ccdDefImportApi.importCaseDefinition(idamHelper.authenticateUser(hrsTester),
                                             authTokenGenerator.generate(), multipartFile);

    }

    public void initHrsTestUser() {
        idamHelper.createUser(hrsTester, hrTesterRoles);
    }

    private InputStream getHrsDefinitionFile() {
        return ClassLoader.getSystemResourceAsStream("CCD_CVP_v.03.xlsx");
    }

    private void createUserRole(String userRole) {
        ccdDefUserRoleApi.createUserRole(new CcdDefUserRoleApi.CreateUserRoleBody(userRole, "PUBLIC"),
                                         idamHelper.authenticateUser(hrsTester), authTokenGenerator.generate());
    }
}
