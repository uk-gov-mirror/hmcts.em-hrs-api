package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefinitionHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

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
    private CcdDefinitionHelper ccdDefinitionHelper;

    private String hrsTester = "hrs.test.user@hmcts.net";
    private List<String> hrTesterRoles = Arrays.asList("caseworker", "caseworker-hrs", "ccd-import");

    @PostConstruct
    public void init() throws Exception {
        initHrsTestUser();
        importHrsDefinitionFile();
    }

    public HearingRecordingDto createRecordingSegment(String url, String filename, int length, int segment) {
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
            length,
            segment,
            "",
            LocalDateTime.now()
        );
    }

    public void importHrsDefinitionFile() throws Exception {
        ccdDefinitionHelper.importDefinitionFile(hrsTester, "caseworker-hrs", getHrsDefinitionFile());
    }

    public InputStream getHrsDefinitionFile() {
        return ClassLoader.getSystemResourceAsStream("CCD_CVP_v.03.xlsx");
    }

    public void initHrsTestUser() {
        idamHelper.createUser(hrsTester, hrTesterRoles);
    }
}
