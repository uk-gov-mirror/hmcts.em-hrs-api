package uk.gov.hmcts.reform.em.hrs.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.em.hrs.dto.HearingRecordingDto;
import uk.gov.hmcts.reform.em.hrs.dto.RecordingFilenameDto;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HearingRecordingController {

    @GetMapping(
        path = "/folders/{name}/hearing-recording-file-names",
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Get recording file names", notes = "Retrieve recording file names for a given folder")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Names of successfully stored recording files")
    })
    public ResponseEntity<RecordingFilenameDto> getHearingRecordingFilenames(@PathVariable(value = "name") String folderName) {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping(
        path = "/folders/{name}/hearing-recording",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Post hearing recording", notes = "Save hearing recording file in the specified folder")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request accepted for asynchronous processing")
    })
    public ResponseEntity<HearingRecordingDto> createHearingRecording(@PathVariable(value = "name") String folderName,
                                                                      @RequestBody HearingRecordingDto request) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
