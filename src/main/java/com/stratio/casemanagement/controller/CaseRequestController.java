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

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCaseRequestById(@PathVariable("id") Long id) {

        log.info("Entering request {}{}/{}", API_VERSION, API_BASE_PATH, id);
        log.debug("Entering CaseRequestController.getCaseRequestById with parameters: {}", id);

        CaseRequest result = mapper.mapBToA(caseRequestService.getCaseRequestById(id));

        log.debug("Exiting CaseRequestController.getCaseRequestById with result: {}" + result);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
