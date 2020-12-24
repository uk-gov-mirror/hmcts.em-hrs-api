package uk.gov.hmcts.reform.em.hrs.service.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.service.DocumentProcessingException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Value("${auth.idam.client.baseUrl}")
    private String idamBaseUrl;

    private final NotificationClient notificationClient;

    private final OkHttpClient http;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String IDAM_USER_DETAILS_ENDPOINT = "/details";

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationClient notificationClient, OkHttpClient http) {
        this.notificationClient = notificationClient;
        this.http = http;
    }

    public void sendEmailNotification(String templateId, String jwt,
                                      String docLink) throws NotificationClientException,
        IOException, DocumentProcessingException {

        String userEmail = getUserEmail(jwt);
        notificationClient.sendEmail(
                templateId,
                userEmail,
                createPersonalisation(docLink),
                "Email Notification: " + userEmail);

        log.info(String.format("Notification email sent to email-Id: %s", userEmail));
    }

    private Map<String, String> createPersonalisation(String docLink) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("document_link", docLink);
        return personalisation;
    }

    private String getUserEmail(String jwt) throws
        IOException, DocumentProcessingException {

        final Request request = new Request.Builder()
                .addHeader("authorization", jwt)
                .url(idamBaseUrl + IDAM_USER_DETAILS_ENDPOINT)
                .get()
                .build();

        final Response response = http.newCall(request).execute();

        if (response.isSuccessful()) {
            JsonNode userDetails = jsonMapper.readValue(response.body().byteStream(), JsonNode.class);
            return userDetails.get("email").asText();
        } else {
            throw new DocumentProcessingException(response.body().string());
        }

    }
}
