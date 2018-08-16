package com.stratio.casemanagement.controller;

import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequestInput;
import com.stratio.casemanagement.model.controller.CaseRequestOutput;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerInboundMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerOutboundMapper;
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
    private final CaseRequestControllerInboundMapper inMapper;
    private final CaseRequestControllerOutboundMapper outMapper;

    @Autowired
    public CaseRequestController(CaseRequestService caseRequestService, CaseRequestControllerInboundMapper inMapper,
                                 CaseRequestControllerOutboundMapper outMapper) {
        this.caseRequestService = caseRequestService;
        this.inMapper = inMapper;
        this.outMapper = outMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> createCaseRequest(@RequestBody CaseRequestInput caseRequest) throws Exception {
        log.info("Entering request (POST) {}{}", API_VERSION, API_BASE_PATH);
        log.debug("Entering CaseRequestController.createCaseRequest with parameters: {}", caseRequest);

        CaseRequestOutput result = outMapper.mapAToB(caseRequestService.insertCaseRequest(inMapper.mapForCreate(caseRequest)));

        log.debug("Exiting CaseRequestController.createCaseRequest with result: {}" + result);

        return ResponseEntity.created(new URI(generateLocationURIForCaseRequest(result))).body(result);

        // TODO: Perform validations on: entityId
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCaseRequestById(@PathVariable("id") Long id) {
        log.info("Entering request (DELETE) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.deleteCaseRequestById with parameters: {}", id);

        int affectedRows = caseRequestService.deleteCaseRequestById(id);

        log.debug("CaseRequestController.deleteCaseRequestById affected rows number: {}", affectedRows);

        return getResponseWithAffectedRows(affectedRows);

        // TODO: No cascade delete on case_raw_data
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCaseRequestById(@PathVariable("id") Long id) {

        log.info("Entering request (GET) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.getCaseRequestById with parameters: {}", id);

        CaseRequestOutput result = outMapper.mapAToB(caseRequestService.getCaseRequestById(id));

        log.debug("Exiting CaseRequestController.getCaseRequestById with result: {}" + result);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateCaseRequestById(@PathVariable("id") Long id, @RequestBody CaseRequestInput caseRequest) {
        log.info("Entering request (PUT) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.updateCaseRequestById with parameters: {}; {}", id, caseRequest);

        int affectedRows = caseRequestService.updateCaseRequestById(id, inMapper.mapForUpdate(caseRequest));

        log.debug("CaseRequestController.updateCaseRequestById affected rows number: {}", affectedRows);

        return getResponseWithAffectedRows(affectedRows);

        // TODO: No update on case_raw_data
    }

    private String generateLocationURIForCaseRequest(CaseRequestOutput response) {
        return SwaggerConfiguration.API_PREFIX + API_VERSION + API_BASE_PATH + "/" + response.getId();
    }

    private ResponseEntity<?> getResponseWithAffectedRows(int affectedRows) {
        if (affectedRows == 1) {
            return ResponseEntity.ok().build();
        } else if (affectedRows == 0) {
            return ResponseEntity.notFound().build();
        } else {
            log.error("Method must affect zero or one rows. Ir affected {} instead", affectedRows);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
