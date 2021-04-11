package uk.gov.hmcts.reform.em.hrs.util;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.text.TextProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.em.hrs.util.IngestionQueue.QUEUE_CAPACITY;

class IngestionQueueTest {
    private final IngestionQueue underTest = IngestionQueue.INSTANCE;

    private final TextProducer textProducer = Fairy.create().textProducer();

    @BeforeEach
    void prepare() {
        underTest.clear();
    }

    @Test
    void testShouldAddItemSuccessfully() {
        final HearingRecordingDto dto = HearingRecordingDto.builder()
            .caseRef(textProducer.randomString(5))
            .build();

        final boolean result = underTest.offer(dto);

        assertThat(result).isTrue();
    }

    @Test
    void testShouldFailToAddItemWhenQueueIsFull() {
        fillUpQueue();
        final HearingRecordingDto dto = HearingRecordingDto.builder()
            .caseRef(textProducer.randomString(5))
            .build();

        final boolean result = underTest.offer(dto);

        assertThat(result).isFalse();
    }

    @Test
    void testShouldReturnEmptyWhenQueueIsEmpty() {
        final HearingRecordingDto result = underTest.poll();

        assertThat(result).isNull();
    }

    @Test
    void testShouldReturnItemWhenQueueHasItems() {
        fillUpQueue();

        final HearingRecordingDto result = underTest.poll();

        assertThat(result).isNotNull();
    }

    private void fillUpQueue() {
        IntStream.rangeClosed(1, QUEUE_CAPACITY).forEach(x -> {
            final HearingRecordingDto dto = HearingRecordingDto.builder()
                .caseRef(textProducer.randomString(5))
                .build();

            underTest.offer(dto);
        });
    }
}
