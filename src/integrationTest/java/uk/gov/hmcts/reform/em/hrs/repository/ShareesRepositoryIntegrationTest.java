package uk.gov.hmcts.reform.em.hrs.repository;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSharee;

import java.time.Clock;
import java.time.LocalDateTime;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.HEARING_RECORDING;
import static uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil.SHAREE_EMAIL_ADDRESS;

class ShareesRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    private static final String EMAIL_ADDRESS = "test@testEmail.com";
    @Inject
    private ShareesRepository underTest;

    @Test
    void testShouldSaveSharee() {
        final HearingRecordingSharee hearingRecordingSharee = HearingRecordingSharee.builder()
            .hearingRecording(HEARING_RECORDING)
            .shareeEmail(SHAREE_EMAIL_ADDRESS).build();

        final HearingRecordingSharee savedSharee = underTest.save(hearingRecordingSharee);

        assertThat(savedSharee).satisfies(x -> {
            assertThat(x.getShareeEmail()).isEqualTo(SHAREE_EMAIL_ADDRESS);
            assertThat(x.getHearingRecording()).isNotNull();
        });
    }

    @Test
    void testAuditingFieldsArePopulated() {
        final LocalDateTime preTest = LocalDateTime.now(Clock.systemDefaultZone());
        final HearingRecordingSharee hearingRecordingSharee = HearingRecordingSharee.builder()
            .hearingRecording(HEARING_RECORDING)
            .shareeEmail(SHAREE_EMAIL_ADDRESS).build();

        final HearingRecordingSharee savedSharee = underTest.save(hearingRecordingSharee);

        final LocalDateTime postTest = LocalDateTime.now(Clock.systemDefaultZone());
        assertThat(savedSharee).satisfies(x -> {
            assertThat(x.getSharedOn()).isBetween(preTest, postTest);
            //assertThat(x.getSharedByRef()).isNotNull();
        });
    }
}
