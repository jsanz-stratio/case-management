package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CaseRequestServiceDefault implements CaseRequestService {

    private final CaseRequestRepository caseRequestRepository;
    private final CaseRequestServiceRepositoryMapper mapper;

    @Autowired
    public CaseRequestServiceDefault(CaseRequestRepository caseRequestRepository, CaseRequestServiceRepositoryMapper mapper) {
        this.caseRequestRepository = caseRequestRepository;
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

        log.debug("Exiting CaseRequestServiceDefault.getCaseRequestById with result: {}", result);

        return result;
    }

    @Override
    @Transactional
    public CaseRequest insertCaseRequest(CaseRequest caseRequest) {
        log.debug("Entering CaseRequestServiceDefault.insertCaseRequest with parameters: {}", caseRequest);

        setDatesAtCreationTime(caseRequest);

        com.stratio.casemanagement.model.repository.CaseRequest repositoryCaseRequest = mapper.mapAToB(caseRequest);
        caseRequestRepository.insertCaseRequest(repositoryCaseRequest);
        CaseRequest result = mapper.mapBToA(repositoryCaseRequest);

        log.debug("Exiting CaseRequestServiceDefault.insertCaseRequest with result: {}", result);

        return result;
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
