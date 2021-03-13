package uk.gov.hmcts.reform.em.hrs.service.ccd;

import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

public class CaseDataContentCreator {

    public CaseDataContent createStartCaseDataContent(StartEventResponse startEventResponse,
                                                       RecordingFilenameDto recordingFilenameDto) {
        return CaseDataContent.builder()
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .eventToken(startEventResponse.getToken())
            .data(recordingFilenameDto)
            .build();//TODO - NEED TO FIND OUT HOW TO PROPERLY CONSTRUCT CASE DATA
    }


    public CcdCaseDataContent createCcdCaseDataContent(HRCaseUpdateDto caseUpdateDto,
                                                       RecordingFilenameDto recordingFilenameDto) {
        CcdCaseDataContent ccdCaseDataContent = new CcdCaseDataContent();
        ccdCaseDataContent.setEvent(new CcdEvent(caseUpdateDto.getEventId()));
        ccdCaseDataContent.setEventData(caseUpdateDto.getCaseData());
        ccdCaseDataContent.setToken(caseUpdateDto.getEventToken());
        ccdCaseDataContent.setData(caseUpdateDto.getCaseData());//TODO - use the recordingFilename to update case data
        return ccdCaseDataContent;
    }
}
