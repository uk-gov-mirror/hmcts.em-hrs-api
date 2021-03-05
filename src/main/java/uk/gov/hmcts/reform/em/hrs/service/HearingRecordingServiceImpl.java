package uk.gov.hmcts.reform.em.hrs.service;

import uk.gov.hmcts.reform.em.hrs.repository.HearingRecordingRepository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HearingRecordingServiceImpl implements HearingRecordingService {
    private final HearingRecordingRepository hearingRecordingRepository;

    @Inject
    public HearingRecordingServiceImpl(final HearingRecordingRepository hearingRecordingRepository) {
        this.hearingRecordingRepository = hearingRecordingRepository;
    }
}
