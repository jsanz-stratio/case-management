package com.stratio.casemanagement.controller;

import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequest;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerServiceMapper;
import com.stratio.casemanagement.service.CaseRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CaseRequestControllerServiceMapper mapper;

    @Autowired
    public CaseRequestController(CaseRequestService caseRequestService, CaseRequestControllerServiceMapper mapper) {
        this.caseRequestService = caseRequestService;
        this.mapper = mapper;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCaseRequestById(@PathVariable("id") Long id) {

        log.info("Entering request (GET) {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.getCaseRequestById with parameters: {}", id);

        CaseRequest result = mapper.mapBToA(caseRequestService.getCaseRequestById(id));

        log.debug("Exiting CaseRequestController.getCaseRequestById with result: {}" + result);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> createCaseRequest(@RequestBody CaseRequest caseRequest) throws Exception {
        log.info("Entering request (POST) {}{}", API_VERSION, API_BASE_PATH);
        log.debug("Entering CaseRequestController.createCaseRequest with parameters: {}", caseRequest);

        CaseRequest result = mapper.mapBToA(caseRequestService.insertCaseRequest(mapper.mapAToB(caseRequest)));

        log.debug("Exiting CaseRequestController.createCaseRequest with result: {}" + result);

        // TODO: Generate location
        return ResponseEntity.created(new URI("http://example.com")).body(result);

        // TODO: Perform validations!!
        // TODO: Controller advice!
        // TODO: Expose no dates!!
    }
}
