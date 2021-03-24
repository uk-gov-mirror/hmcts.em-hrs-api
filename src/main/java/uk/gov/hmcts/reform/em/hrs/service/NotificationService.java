package uk.gov.hmcts.reform.em.hrs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.exception.JsonDocumentProcessingException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    @Value("${auth.idam.client.baseUrl}")
    private String idamBaseUrl;

    private final NotificationClient notificationClient;

    private final OkHttpClient http;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String IDAM_USER_DETAILS_ENDPOINT = "/details";

    //private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationClient notificationClient, OkHttpClient http) {
        this.notificationClient = notificationClient;
        this.http = http;
    }

    public void sendEmailNotification(String templateId, String jwt,
                                      List<String> docLink, String caseReference,
                                      String createdOn, UUID shareesId) throws NotificationClientException,
        IOException, JsonDocumentProcessingException {

        String userEmail = getUserEmail(jwt);
        notificationClient.sendEmail(
                templateId,
                userEmail,
                createPersonalisation(docLink, caseReference, createdOn),
//                "Email Notification: " + userEmail);
                "hrs-grant-" + shareesId);

        //log.info(String.format("Notification email sent to email-Id: %s", userEmail));
    }

    private Map<String, Object> createPersonalisation(List<String> docLink, String caseReference, String createdOn) {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put("document_link", docLink);
        personalisation.put("case_reference", caseReference);
        personalisation.put("created_on", createdOn) ;
        return personalisation;
    }

    private String getUserEmail(String jwt) throws
        IOException, JsonDocumentProcessingException {

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
            throw new JsonDocumentProcessingException(response.body().string());
        }

    }
}
