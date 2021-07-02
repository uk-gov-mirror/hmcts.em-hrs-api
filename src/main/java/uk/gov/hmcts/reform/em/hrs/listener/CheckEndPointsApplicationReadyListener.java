package uk.gov.hmcts.reform.em.hrs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.storage.HearingRecordingStorage;

@Component
public class CheckEndPointsApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckEndPointsApplicationReadyListener.class);

    @Autowired
    HearingRecordingStorage hearingRecordingStorage;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOGGER.info("Application is Ready");

        try {
            String report = hearingRecordingStorage.getStorageReport();
            LOGGER.info("StorageReport: {}",report);
        }
        catch (Exception e) {
            LOGGER.error("Unable to verify storage connectivity: {}",e.getMessage());
        }

    }


}
