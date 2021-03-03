package uk.gov.hmcts.reform.em.hrs.repository;

import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;

import java.util.List;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HearingRecordingRepositoryTest {
    @Inject
    private HearingRecordingRepository hearingRecordingRepository;

    private static final String ZERO_ITEM_FOLDER = "folder-0";

    //    @Test
    void testShouldReturnEmptySetWhenDatabaseIsEmpty() {
        final List<HearingRecording> recordings = hearingRecordingRepository.findByFolder(ZERO_ITEM_FOLDER);

        assertThat(recordings).isEmpty();
    }
}
