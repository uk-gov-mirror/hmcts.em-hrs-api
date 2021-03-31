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
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecording;
import uk.gov.hmcts.reform.em.hrs.domain.HearingRecordingSegment;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;
import uk.gov.hmcts.reform.em.hrs.service.FolderService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingSegmentService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingService;
import uk.gov.hmcts.reform.em.hrs.service.HearingRecordingShareeService;
import uk.gov.hmcts.reform.em.hrs.service.ShareService;
import uk.gov.hmcts.reform.em.hrs.service.ccd.CaseUpdateService;

import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;


@RestController
public class HearingRecordingController {

    private final FolderService folderService;
    private final HearingRecordingShareeService shareeService;
    private final HearingRecordingService recordingService;
    private final HearingRecordingSegmentService segmentService;
    private final ShareService shareService;
    private final CaseUpdateService caseUpdateService;

    @Inject
    public HearingRecordingController(final FolderService folderService,
                                      final CaseUpdateService caseUpdateService,
                                      final HearingRecordingShareeService shareeService,
                                      final HearingRecordingService recordingService,
                                      final HearingRecordingSegmentService segmentService,
                                      final ShareService shareService) {
        this.folderService = folderService;
        this.caseUpdateService = caseUpdateService;
        this.shareeService = shareeService;
        this.recordingService = recordingService;
        this.segmentService = segmentService;
        this.shareService = shareService;
    }

    @GetMapping(
        path = "/folders/{name}",
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
        path = "/segments",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Post hearing recording segment", notes = "Save hearing recording segment")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request accepted for asynchronous processing")
    })
    public ResponseEntity<HearingRecordingDto> createHearingRecording(
            @RequestBody HearingRecordingDto hearingRecordingDto) {

        Optional<HearingRecording> hearingRecording =
            recordingService.findByRecordingRef(hearingRecordingDto.getRecordingRef());

        Long caseId = caseUpdateService
            .addRecordingToCase(hearingRecordingDto, hearingRecording.map(HearingRecording::getCcdCaseId));
        segmentService.persistRecording(hearingRecordingDto, hearingRecording, caseId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(
        path = "/sharees",
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
    public ResponseEntity<HearingRecordingDto> shareHearingRecording(@RequestBody CaseDetails request) {

        //TODO - @RequestBody should be HttpServletRequest but will need to configure unit test

        Optional<HearingRecording> hearingRecording = recordingService.findByCaseId(request.getId());

        String emailAddress = request.getData().get("recipientEmailAddress").toString();

        if (hearingRecording.isPresent()) {
            shareeService.createAndSaveEntry(emailAddress, hearingRecording.get());
            Set<HearingRecordingSegment> segments = hearingRecording.get().getSegments();
            // TODO - Trigger the ShareService with the info
            // Return ResponseEntity.ok(shareService.executeNotify(hearingRecordingSegments, request));
        }

        return ResponseEntity.ok().build();

    }

    @GetMapping(
        path = "/documents/hearing-recordings/{recordingRef}/segments/{segment}",
        produces = APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Get hearing recording file",
                  notes = "Return hearing recording file from the specified folder")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Return the requested hearing recording")})
    public ResponseEntity getHearingRecording(@PathVariable("recordingRef") String recordingRef,
                                              @PathVariable("segment") String segment) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
