package uk.gov.hmcts.reform.em.hrs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;

import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Nonnull;

public enum IngestionQueue {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionQueue.class);
    private LinkedBlockingQueue<HearingRecordingDto> queue;

    public static Builder builder() {
        return new Builder();
    }

    private void init(@Nonnull final Integer queueCapacity) {
        queue = new LinkedBlockingQueue<>(queueCapacity);
    }

    public boolean offer(final HearingRecordingDto dto) {

        try {
            return queue.offer(dto);
        } catch (TaskRejectedException taskRejectedException) {
            //queue.offer supposedly wraps this issue - but lots of exceptions got logged during some tests when blob
            //store was not connected properly
            LOGGER.info("Task Rejected: {}", taskRejectedException.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.info("Exception offering task to queue: {}", e.getMessage());
            return false;
        }

    }

    public HearingRecordingDto poll() {
        return queue.poll();
    }

    public void clear() {
        queue.clear();
    }

    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    public static class Builder {
        private static final int DEFAULT_CAPACITY = 1000;
        private int capacity;

        private Builder() {
        }

        public Builder capacity(final int capacity) {
            this.capacity = capacity;
            return this;
        }

        public IngestionQueue build() {
            final IngestionQueue instance = IngestionQueue.INSTANCE;
            if (capacity == 0) {
                instance.init(DEFAULT_CAPACITY);
            } else {
                instance.init(capacity);
            }

            return instance;
        }
    }

}
