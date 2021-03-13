package uk.gov.hmcts.reform.em.hrs.service.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

import java.io.IOException;

public class CcdDataStoreApiClient {

    private final Logger log = LoggerFactory.getLogger(CcdDataStoreApiClient.class);

    private final OkHttpClient http;
    private final AuthTokenGenerator authTokenGenerator;
    private final CaseDataContentCreator caseDataContentCreator;

    private final String ccdDataBaseUrl;
    private final String ADD_RECORDING_FILE_EVENT_ENDPOINT = "/cases/%s/event-triggers/addRecordingFile";
    private final String ccdUpdateCasePath = "/cases/%s/events";
    private final ObjectMapper objectMapper;


    public CcdDataStoreApiClient(OkHttpClient http,
                                 AuthTokenGenerator authTokenGenerator,
                                 CaseDataContentCreator caseDataContentCreator,
                                 @Value("${ccd.data.api.url}") String ccdDataBaseUrl,
                                 ObjectMapper objectMapper) {
        this.http = http;
        this.authTokenGenerator = authTokenGenerator;
        this.caseDataContentCreator = caseDataContentCreator;
        this.ccdDataBaseUrl = ccdDataBaseUrl;
        this.objectMapper = objectMapper;
    }

    /**
     * @param caseId - case id
     * @param jwt - authentication
     * @return - DTO with CCD case
     */
    public HRCaseUpdateDto getHRCaseData(String caseId, String jwt) {
        final Request request = new Request.Builder()
            .addHeader("Authorization", jwt)
            .addHeader("experimental", "true")
            .addHeader("ServiceAuthorization", authTokenGenerator.generate())
            .url(String.format(ccdDataBaseUrl + ADD_RECORDING_FILE_EVENT_ENDPOINT, caseId))
            .get()
            .build();

        log.info(String.format("Ccd Event Trigger URL : %s", request.url()));
        try {
            final Response response = http.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CcdUpdateException(response.code(), response.body().string(), "Creation of event-trigger failed");
            }
            return new HRCaseUpdateDto(objectMapper.readTree(response.body().charStream()));
        } catch (IOException e) {
            throw new CcdUpdateException(500, null, String.format("IOException: %s", e.getMessage()));
        }
    }

    /**
     *
     * @param hrCaseUpdateDto - callbackDTO
     * @param jwt - authentication
     */
    public void updateHRCaseData(HRCaseUpdateDto hrCaseUpdateDto, String jwt, RecordingFilenameDto recordingFilenameDto) {
        try {
            final CcdCaseDataContent caseDataContent =
                caseDataContentCreator.createCcdCaseDataContent(hrCaseUpdateDto, recordingFilenameDto);
            final RequestBody body = RequestBody.create(objectMapper.writeValueAsString(caseDataContent),
                                                        MediaType.get("application/json"));
            final Request updateRequest = new Request.Builder()
                .addHeader("Authorization", jwt)
                .addHeader("experimental", "true")
                .addHeader("ServiceAuthorization", authTokenGenerator.generate())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.get("application/json").toString())
                .url(String.format(ccdDataBaseUrl + ccdUpdateCasePath, hrCaseUpdateDto.getCaseId()))
                .post(body)
                .build();

            log.info(String.format("Ccd Update URL :  %s", updateRequest.url()));

            final Response updateResponse = http.newCall(updateRequest).execute();

            if (!updateResponse.isSuccessful()) {
                throw new CcdUpdateException(updateResponse.code(), updateResponse.body().string(), "Update of case data failed");
            }
        } catch (IOException e) {
            throw new CcdUpdateException(500, null, String.format("IOException: %s", e.getMessage()));
        }
    }
}
