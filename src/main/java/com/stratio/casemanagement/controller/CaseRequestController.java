package com.stratio.casemanagement.controller;

import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequestRequest;
import com.stratio.casemanagement.model.controller.CaseRequestResponse;
import com.stratio.casemanagement.model.mapper.CaseRequestRequestControllerToCaseRequestServiceMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceToCaseRequestResponseControllerMapper;
import com.stratio.casemanagement.service.CaseRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.stratio.casemanagement.controller.CaseRequestController.API_BASE_PATH;
import static com.stratio.casemanagement.controller.CaseRequestController.API_VERSION;

@RestController
@RequestMapping(value = SwaggerConfiguration.API_PREFIX + API_VERSION + API_BASE_PATH)
@Slf4j
public class CaseRequestController {
    public static final String API_VERSION = "/v1";
    public static final String API_BASE_PATH = "/case-requests";

    private final CaseRequestService caseRequestService;
    private final CaseRequestRequestControllerToCaseRequestServiceMapper inMapper;
    private final CaseRequestServiceToCaseRequestResponseControllerMapper outMapper;

    @Autowired
    public CaseRequestController(CaseRequestService caseRequestService, CaseRequestRequestControllerToCaseRequestServiceMapper inMapper,
                                 CaseRequestServiceToCaseRequestResponseControllerMapper outMapper) {
        this.caseRequestService = caseRequestService;
        this.inMapper = inMapper;
        this.outMapper = outMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> createCaseRequest(@RequestBody CaseRequestRequest caseRequest) throws Exception {
        log.info("Entering request (POST) {}{}", API_VERSION, API_BASE_PATH);
        log.debug("Entering CaseRequestController.createCaseRequest with parameters: {}", caseRequest);

        CaseRequestResponse result = outMapper.mapAToB(caseRequestService.insertCaseRequest(inMapper.mapAToB(caseRequest)));

        log.debug("Exiting CaseRequestController.createCaseRequest with result: {}" + result);

        return ResponseEntity.created(new URI(generateLocationURIForCaseRequest(result))).body(result);

        // TODO: Perform validations on: entityId
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCaseRequestById(@PathVariable("id") Long id) {

        log.info("Entering request (GET) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.getCaseRequestById with parameters: {}", id);

        CaseRequestResponse result = outMapper.mapAToB(caseRequestService.getCaseRequestById(id));

        log.debug("Exiting CaseRequestController.getCaseRequestById with result: {}" + result);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCaseRequestById(@PathVariable("id") Long id) {
        log.info("Entering request (DELETE) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.deleteCaseRequestById with parameters: {}", id);

        int affectedRows = caseRequestService.deleteCaseRequestById(id);

        log.debug("CaseRequestController.deleteCaseRequestById affected rows number: {}" + affectedRows);

        if (affectedRows == 1) {
            return ResponseEntity.ok().build();
        } else if (affectedRows == 0) {
            return ResponseEntity.notFound().build();
        } else {
            log.error("CaseRequestController.deleteCaseRequestById must delete zero or one rows. Ir deleted {} instead", affectedRows);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateLocationURIForCaseRequest(CaseRequestResponse response) {
        return SwaggerConfiguration.API_PREFIX + API_VERSION + API_BASE_PATH + "/" + response.getId();
    }
}
