package uk.gov.hmcts.reform.em.hrs.util;

import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.concurrent.LinkedBlockingQueue;

public enum IngestionQueue {
    INSTANCE;

    static final int QUEUE_CAPACITY = 1000;
    private static final LinkedBlockingQueue<HearingRecordingDto> QUEUE = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    public boolean offer(final HearingRecordingDto dto) {
        return QUEUE.offer(dto);
    }

    public HearingRecordingDto poll() {
        return QUEUE.poll();
    }

    public void clear() {
        QUEUE.clear();
    }

}
