package uk.gov.hmcts.reform.em.hrs.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.dto.HearingSource;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CcdDataStoreApiClient;
import uk.gov.hmcts.reform.em.test.ccddefinition.CcdDefinitionHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ExtendedCcdHelper {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private CcdDefinitionHelper ccdDefinitionHelper;

    @Autowired
    private CcdDataStoreApiClient ccdDataStoreApiClient;

    private String hrsTester = "hrs.test.user@hmcts.net";
    private List<String> hrTesterRoles = Stream.of("caseworker", "caseworker-hrs", "ccd-import").collect(Collectors.toList());

    @PostConstruct
    public void init() throws Exception {
        initHRSTestUser();
        importHRSDefinitionFile();
    }

    public CaseDetails createHRSCase(HearingRecordingDto hearingRecordingDto) {
        return ccdDataStoreApiClient.createHRCase(hearingRecordingDto);
    }

    public CaseDetails updateHRSCase(HearingRecordingDto hearingRecordingDto, String caseId) {
        return ccdDataStoreApiClient.updateHRCaseData(caseId, hearingRecordingDto);
    }

    public HearingRecordingDto createRecordingSegment(String url, String filename, int length, int segment) {
        return new HearingRecordingDto(
            "hearing-12-family-probate-morning",
            HearingSource.CVP,
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

    public void importHRSDefinitionFile() throws Exception {
        ccdDefinitionHelper.importDefinitionFile(
            hrsTester,
            "caseworker-hrs",
            getHRSDefinitionFile());
    }

    public InputStream getHRSDefinitionFile() {
        return ClassLoader.getSystemResourceAsStream("CCD_CVP_v.03.xlsx");
    }

    public void initHRSTestUser() {
        idamHelper.createUser(hrsTester, hrTesterRoles);
    }
}
