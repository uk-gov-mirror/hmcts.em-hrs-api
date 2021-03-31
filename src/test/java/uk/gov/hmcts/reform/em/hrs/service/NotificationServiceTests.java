package uk.gov.hmcts.reform.em.hrs.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.em.hrs.exception.JsonDocumentProcessingException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTests {

    @Mock
    private NotificationClient notificationClient;

    private NotificationService notificationService;

    @Test
    @Disabled
    public void sendEmailNotificationSuccessful() throws Exception {
        List<String> responses = new ArrayList<>();
        responses.add("{ \"id\": 1, \"email\": \"test@email.com\", \"forename\": \"test\", \"surname\": \"user\" }");
        OkHttpClient http = getMockHttpSuccess(responses);

        setUpNotificationClient(http);

        List<String> docLink = new ArrayList<>();
        docLink.add("string");

        UUID shareesId = UUID.randomUUID();

//        notificationService.sendEmailNotification(
//            "string",
//            "string",
//            docLink,
//            "string",
//            "string",
//            shareesId
//        );
//
//        verify(notificationClient, times(1))
//            .sendEmail(
//                "string",
//                "test@email.com",
//                getPersonalisation(),
//                "hrs-grant-" + shareesId);
    }

    @Test()
    @Disabled
    public void sendEmailNotificationFailure() throws Exception {
        List<String> responses = new ArrayList<>();
        responses.add("{ \"id\": 1, \"email\": \"email@email.com\", \"forename\": \"test\", \"surname\": \"user\" }");
        OkHttpClient http = getMockHttpSuccess(responses);

        setUpNotificationClient(http);

        List<String> docLink = new ArrayList<>();
        docLink.add("string");

        UUID shareesId = UUID.randomUUID();

        when(notificationClient.sendEmail(
            "string",
            "email@email.com",
            getPersonalisation(),
            "hrs-grant-" + shareesId
        )).thenThrow(NotificationClientException.class);

//        Assertions.assertThrows(NotificationClientException.class, () -> {
//            notificationService.sendEmailNotification(
//                "string",
//                "string",
//                docLink,
//                "string",
//                "string",
//                shareesId
//            );
//        });

    }

    @Test
    @Disabled
    public void getUserDetailsFailure() throws NotificationClientException {
        List<String> responses = new ArrayList<>();
        responses.add("{ \"id\": 1, \"email\": \"email@email.com\", \"forename\": \"test\", \"surname\": \"user\" }");
        OkHttpClient http = getMockHttpFailures(responses);

        setUpNotificationClient(http);

        List<String> docLink = new ArrayList<>();
        docLink.add("string");

        UUID shareesId = UUID.randomUUID();

//        Assertions.assertThrows(JsonDocumentProcessingException.class, () -> {
//            notificationService.sendEmailNotification(
//                "string",
//                "string",
//                docLink,
//                "string",
//                "string",
//                shareesId
//            );
//        });
    }

    public void setUpNotificationClient(OkHttpClient http) {
        MockitoAnnotations.initMocks(this);
        notificationService = null;//new NotificationService(notificationClient, http);
        ReflectionTestUtils.setField(notificationService, "idamBaseUrl", "http://localhost:4501");
    }

    public OkHttpClient getMockHttpSuccess(List<String> body) {
        Iterator<String> iterator = body.iterator();

        return new OkHttpClient
            .Builder()
            .addInterceptor(chain -> new Response.Builder()
                .body(ResponseBody.create(MediaType.get("application/json"), iterator.next()))
                .request(chain.request())
                .message("")
                .code(200)
                .protocol(Protocol.HTTP_2)
                .build())
            .build();
    }

    public OkHttpClient getMockHttpFailures(List<String> body) {
        Iterator<String> iterator = body.iterator();

        return new OkHttpClient
            .Builder()
            .addInterceptor(chain -> new Response.Builder()
                .body(ResponseBody.create(MediaType.get("application/json"), iterator.next()))
                .request(chain.request())
                .message("")
                .code(500)
                .protocol(Protocol.HTTP_2)
                .build())
            .build();
    }

    public HashMap<String, Object> getPersonalisation() {
        List<String> docLink = new ArrayList<>();
        docLink.add("string");
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put("document_link", docLink);
        personalisation.put("case_reference", "string");
        personalisation.put("created_on", "string");
        return personalisation;
    }
}
