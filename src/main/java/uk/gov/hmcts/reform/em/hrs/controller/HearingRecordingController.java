package uk.gov.hmcts.reform.em.hrs.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingSegmentService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingShareesService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CaseUpdateService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HearingRecordingController {
    private final FolderService folderService;
    private final HearingRecordingShareesService hearingRecordingShareesService;
    private final HearingRecordingService hearingRecordingService;
    private final HearingRecordingSegmentService hearingRecordingSegmentService;
    private final ShareService shareService;
    private final CaseUpdateService caseUpdateService;

    @Inject
    public HearingRecordingController(final FolderService folderService,
                                      final CaseUpdateService caseUpdateService,
                                      final HearingRecordingShareesService hearingRecordingShareesService,
                                      final HearingRecordingService hearingRecordingService,
                                      final HearingRecordingSegmentService hearingRecordingSegmentService,
                                      final ShareService shareService) {
        this.folderService = folderService;
        this.caseUpdateService = caseUpdateService;
        this.hearingRecordingShareesService = hearingRecordingShareesService;
        this.hearingRecordingService = hearingRecordingService;
        this.hearingRecordingSegmentService = hearingRecordingSegmentService;
        this.shareService = shareService;
    }

    @GetMapping(
        path = "/folders/{name}/hearing-recording-file-names",
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Get recording file names", notes = "Retrieve recording file names for a given folder")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Names of successfully stored recording files")
    })
    public ResponseEntity<RecordingFilenameDto> getFilenames(@PathVariable("name") final String folderName) {
        final RecordingFilenameDto recordingFilenameDto = new RecordingFilenameDto(
            folderName,
            folderService.getStoredFiles(folderName)
        );
        return ResponseEntity
            .ok()
            .contentType(APPLICATION_JSON)
            .body(recordingFilenameDto);
    }

    @PostMapping(
        path = "/folder/{folder}/hearing-recording/{recordingRef}/segment/{segment}",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Post hearing recording", notes = "Save hearing recording file in the specified folder")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request accepted for asynchronous processing")
    })
    public ResponseEntity<HearingRecordingDto> createHearingRecording(@PathVariable("folder") String folderName,
                                                                      @PathVariable("recordingRef") String recordingRef,
                                                                      @PathVariable("segment") String segment,
                                                                      @RequestBody HearingRecordingDto request) {

        caseUpdateService.addRecordingToCase(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //    @GetMapping(
    //        path = "/folders/{name}/hearing-recording/{id}/segment/{segmentId}",
    //        produces = APPLICATION_OCTET_STREAM_VALUE
    //    )
    //    @ResponseBody
    //    @ApiOperation(value = "Get hearing recording",
    //    notes = "Return hearing recording file from the specified folder")
    //    @ApiResponses(value = {
    //        @ApiResponse(code = 200, message = "Return the requested hearing recording")
    //    })
    //    public ResponseEntity<HearingRecordingDto> getHearingRecording(@PathVariable("name") String folderName,
    //                                                                      @PathVariable("id") String recordingId,
    //                                                                   @PathVariable("segmentId") String segmentId) {
    //        return new ResponseEntity<>(HttpStatus.OK);
    //    }
    //


    @PostMapping(
        path = "/folders/{name}/hearing-recording/{id}/access-right",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Create permissions record", notes = "Create permissions record to the specified "
        + "hearing recording and notify user with the link to the resource via email")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return the location of the resource being granted "
            + "access to (the download link)"),
        @ApiResponse(code = 404, message = "Not Found")
    })
    public ResponseEntity<HearingRecordingDto> shareHearingRecording(@RequestBody String request,
                                                                     @PathVariable("name") String folderName,
                                                                     @PathVariable("id") UUID recordingId) {

        //TODO - @RequestBody should be HttpServletRequest but will need to configure unit test

        // Find the associated Hearing Recording
        Optional<HearingRecording> hearingRecording = hearingRecordingService.findOne(recordingId);

        // Check if email is valid
        // String emailAddress = request.getParameter("emailAddress");
        String emailAddress = request;

        // Save the hearingRecordingSharee
        if (hearingRecording.isPresent()) {
            hearingRecordingShareesService.createAndSaveEntry(emailAddress, hearingRecording.get());
        }

        // Get the Hearing Recording Segments associated with the Hearing Recording
        List<HearingRecordingSegment> hearingRecordingSegments = hearingRecordingSegmentService.findByRecordingId(
            recordingId);

        // TODO - Trigger the ShareService with the info
        // Return ResponseEntity.ok(shareService.executeNotify(hearingRecordingSegments, request));


        return ResponseEntity.ok().build();

    }
}
