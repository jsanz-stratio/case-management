package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRawDataRepository;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CaseRequestServiceDefault implements CaseRequestService {

    private final CaseRequestRepository caseRequestRepository;
    private final CaseRawDataRepository caseRawDataRepository;
    private final ObjectMapper jacksonObjectMapper;
    private final CaseRequestServiceRepositoryMapper mapper;

    @Autowired
    public CaseRequestServiceDefault(CaseRequestRepository caseRequestRepository, CaseRawDataRepository caseRawDataRepository,
                                     ObjectMapper jacksonObjectMapper, CaseRequestServiceRepositoryMapper mapper) {
        this.caseRequestRepository = caseRequestRepository;
        this.caseRawDataRepository = caseRawDataRepository;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.mapper = mapper;
    }

    @Override
    public int deleteCaseRequestById(Long id) {
        log.debug("Entering CaseRequestServiceDefault.deleteCaseRequestById with parameters: {}", id);

        int affectedRows = caseRequestRepository.deleteCaseRequestById(id);

        log.debug("Exiting CaseRequestServiceDefault.deleteCaseRequestById with result: {}", affectedRows);

        return affectedRows;
    }

    @Override
    public CaseRequest getCaseRequestById(Long id) {
        log.debug("Entering CaseRequestServiceDefault.getCaseRequestById with parameters: {}", id);

        CaseRequest result = mapper.mapBToA(caseRequestRepository.getCaseRequestById(id));
        if (result != null) {
            insertCaseRawDataInResult(id, result);
        }

        log.debug("Exiting CaseRequestServiceDefault.getCaseRequestById with result: {}", result);

        return result;
    }

    @Override
    @Transactional
    public CaseRequest insertCaseRequest(CaseRequest inputCaseRequest) {
        log.debug("Entering CaseRequestServiceDefault.insertCaseRequest with parameters: {}", inputCaseRequest);

        setDatesAtCreationTime(inputCaseRequest);

        com.stratio.casemanagement.model.repository.CaseRequest repositoryCaseRequest = mapper.mapAToB(inputCaseRequest);
        caseRequestRepository.insertCaseRequest(repositoryCaseRequest);
        CaseRequest outputCaseRequest = mapper.mapBToA(repositoryCaseRequest);

        if (StringUtils.hasText(inputCaseRequest.getCaseRawData())) {
            CaseRawData caseRawData = buildCaseRawData(inputCaseRequest, outputCaseRequest);
            caseRawDataRepository.insertCaseRawData(caseRawData);
            outputCaseRequest.setCaseRawData(inputCaseRequest.getCaseRawData());
        }

        log.debug("Exiting CaseRequestServiceDefault.insertCaseRequest with result: {}", outputCaseRequest);

        return outputCaseRequest;
    }

    @Override
    @Transactional
    public int updateCaseRequestById(Long id, CaseRequest caseRequest) {
        log.debug("Entering CaseRequestServiceDefault.updateCaseRequestById with parameters: {}", caseRequest);

        setDatesAtModificationTime(caseRequest);

        int affectedRows = caseRequestRepository.updateCaseRequestById(id, mapper.mapAToB(caseRequest));

        log.debug("Exiting CaseRequestServiceDefault.updateCaseRequestById with result: {}", affectedRows);

        return affectedRows;
    }

    private CaseRawData buildCaseRawData(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest) {
        CaseRawData caseRawData = new CaseRawData();
        caseRawData.setCaseId(outputCaseRequest.getId());
        caseRawData.setRaw(inputCaseRequest.getCaseRawData());
        return caseRawData;
    }

    private void insertCaseRawDataInResult(Long id, CaseRequest result) {
        CaseRawData caseRawDataById = caseRawDataRepository.getCaseRawDataById(id);
        result.setCaseRawData(caseRawDataById != null ? caseRawDataById.getRaw() : null);
    }

    private void setDatesAtCreationTime(CaseRequest caseRequest) {
        LocalDateTime now = LocalDateTime.now();
        caseRequest.setCreationDate(now);
        caseRequest.setModificationDate(now);
    }

    private void setDatesAtModificationTime(CaseRequest caseRequest) {
        LocalDateTime now = LocalDateTime.now();
        caseRequest.setModificationDate(now);
    }
}
