package uk.gov.hmcts.reform.em.hrs.storage;

import java.util.Set;
import javax.inject.Named;

@Named
public class DefaultHearingRecordingStorage implements HearingRecordingStorage {

    @Override
    public Set<String> findByFolder(String folderName) {
        return null;
    }
}
