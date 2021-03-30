package uk.gov.hmcts.reform.em.hrs.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.service.tokens.SecurityClient;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationClient notificationClient;
    private final SecurityClient securityClient;


    //private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationClient notificationClient,
                               SecurityClient securityClient) {
        this.notificationClient = notificationClient;
        this.securityClient = securityClient;
    }

    public void sendEmailNotification(String templateId, String docLink) throws NotificationClientException {

        String userEmail = securityClient.getUserEmail();
        notificationClient.sendEmail(
                templateId,
                userEmail,
                createPersonalisation(docLink),
                "Email Notification: " + userEmail);

        //log.info(String.format("Notification email sent to email-Id: %s", userEmail));
    }

    private Map<String, String> createPersonalisation(String docLink) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("document_link", docLink);
        return personalisation;
    }
}
