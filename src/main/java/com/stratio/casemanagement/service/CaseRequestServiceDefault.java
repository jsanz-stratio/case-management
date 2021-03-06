package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.repository.CaseApplication;
import com.stratio.casemanagement.model.repository.CaseParticipant;
import com.stratio.casemanagement.model.repository.CaseRawAttachment;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CaseRequestServiceDefault implements CaseRequestService {

    private final CaseRequestRepository caseRequestRepository;
    private final CaseRawDataRepository caseRawDataRepository;
    private final CaseRawAttachmentRepository caseRawAttachmentRepository;
    private final CaseParticipantRepository caseParticipantRepository;
    private final CaseApplicationRepository caseApplicationRepository;
    private final CaseRequestServiceRepositoryMapper mapper;

    @Autowired
    public CaseRequestServiceDefault(CaseRequestRepository caseRequestRepository, CaseRawDataRepository caseRawDataRepository,
                                     CaseRawAttachmentRepository caseRawAttachmentRepository, CaseParticipantRepository caseParticipantRepository,
                                     CaseApplicationRepository caseApplicationRepository, CaseRequestServiceRepositoryMapper mapper) {
        this.caseRequestRepository = caseRequestRepository;
        this.caseRawDataRepository = caseRawDataRepository;
        this.caseRawAttachmentRepository = caseRawAttachmentRepository;
        this.caseParticipantRepository = caseParticipantRepository;
        this.caseApplicationRepository = caseApplicationRepository;
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

        CaseRequest result = mapper.mapCaseRequestFromRepoToService(caseRequestRepository.getCaseRequestById(id));
        if (result != null) {
            insertCaseRawDataInResult(id, result);
            insertCaseRawAttachmentsInResult(id, result);
            insertParticipantsInResult(id, result);
        }

        log.debug("Exiting CaseRequestServiceDefault.getCaseRequestById with result: {}", result);

        return result;
    }

    @Override
    @Transactional
    public CaseRequest insertCaseRequest(CaseRequest inputCaseRequest) {
        log.debug("Entering CaseRequestServiceDefault.insertCaseRequest with parameters: {}", inputCaseRequest);

        setDatesAtCreationTime(inputCaseRequest);

        com.stratio.casemanagement.model.repository.CaseRequest repositoryCaseRequest = mapper.mapCaseRequestFromServiceToRepo(inputCaseRequest);
        caseRequestRepository.insertCaseRequest(repositoryCaseRequest);
        CaseRequest outputCaseRequest = mapper.mapCaseRequestFromRepoToService(repositoryCaseRequest);

        Long generatedCaseRequestId = outputCaseRequest.getId();

        if (StringUtils.hasText(inputCaseRequest.getCaseRawData())) {
            CaseRawData caseRawData = buildCaseRawData(inputCaseRequest, outputCaseRequest);
            caseRawDataRepository.insertCaseRawData(caseRawData);
            outputCaseRequest.setCaseRawData(inputCaseRequest.getCaseRawData());
        }

        if (!CollectionUtils.isEmpty(inputCaseRequest.getCaseRawAttachments())) {
            inputCaseRequest.getCaseRawAttachments().stream().filter(Objects::nonNull).forEach(rawAt -> {
                CaseRawAttachment repoRawAt = mapper.mapRawAttachmentFromServiceToRepo(rawAt);
                repoRawAt.setCaseId(generatedCaseRequestId);
                caseRawAttachmentRepository.insertCaseRawAttachment(repoRawAt);
                com.stratio.casemanagement.model.service.CaseRawAttachment outputRawAt = mapper.mapRawAttachmentFromRepoToService(repoRawAt);
                outputCaseRequest.addCaseRawAttachment(outputRawAt);
            });
        }

        if (StringUtils.hasText(inputCaseRequest.getCaseParticipant())) {
            CaseParticipant caseParticipant = buildParticipant(inputCaseRequest, outputCaseRequest);
            caseParticipantRepository.insertCaseParticipant(caseParticipant);
            outputCaseRequest.setCaseParticipant(inputCaseRequest.getCaseParticipant());
        }

        if (!CollectionUtils.isEmpty(inputCaseRequest.getCaseApplications())) {
            inputCaseRequest.getCaseApplications().stream().filter((Objects::nonNull)).forEach(cApp -> {
                CaseApplication repoCApp = mapper.mapApplicationFromServiceToRepo(cApp);
                repoCApp.setCaseId(generatedCaseRequestId);
                caseApplicationRepository.insertCaseApplication(repoCApp);
                com.stratio.casemanagement.model.service.CaseApplication outputCApp = mapper.mapApplicationFromRepoToService(repoCApp);
                outputCaseRequest.addCaseApplication(outputCApp);
            });
        }

        log.debug("Exiting CaseRequestServiceDefault.insertCaseRequest with result: {}", outputCaseRequest);

        return outputCaseRequest;
    }

    @Override
    @Transactional
    public int updateCaseRequestById(Long id, CaseRequest caseRequest) {
        log.debug("Entering CaseRequestServiceDefault.updateCaseRequestById with parameters: {}", caseRequest);

        setDatesAtModificationTime(caseRequest);

        int affectedRows = caseRequestRepository.updateCaseRequestById(id, mapper.mapCaseRequestFromServiceToRepo(caseRequest));

        log.debug("Exiting CaseRequestServiceDefault.updateCaseRequestById with result: {}", affectedRows);

        return affectedRows;
    }

    private CaseRawData buildCaseRawData(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest) {
        CaseRawData caseRawData = new CaseRawData();
        caseRawData.setCaseId(outputCaseRequest.getId());
        caseRawData.setRaw(inputCaseRequest.getCaseRawData());
        return caseRawData;
    }

    private CaseParticipant buildParticipant(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest) {
        CaseParticipant caseParticipant = new CaseParticipant();
        caseParticipant.setCaseId(outputCaseRequest.getId());
        caseParticipant.setParticipantsData(inputCaseRequest.getCaseParticipant());
        return caseParticipant;
    }

    private void insertCaseRawAttachmentsInResult(Long id, CaseRequest result) {
        List<CaseRawAttachment> caseRawAttachmenListByCaseId = caseRawAttachmentRepository.getCaseRawAttachmentListByCaseId(id);
        result.setCaseRawAttachments(mapper.mapRawAttachmentListFromRepoToService(caseRawAttachmenListByCaseId));
    }

    private void insertCaseRawDataInResult(Long id, CaseRequest result) {
        CaseRawData caseRawDataById = caseRawDataRepository.getCaseRawDataById(id);
        result.setCaseRawData(caseRawDataById != null ? caseRawDataById.getRaw() : null);
    }

    private void insertParticipantsInResult(Long id, CaseRequest result) {
        CaseParticipant caseParticipantById = caseParticipantRepository.getCaseParticipantById(id);
        result.setCaseParticipant(caseParticipantById != null ? caseParticipantById.getParticipantsData() : null);
    }

    private void setDatesAtCreationTime(CaseRequest caseRequest) {
        LocalDateTime now = LocalDateTime.now();
        caseRequest.setCreationDate(now);
        caseRequest.setModificationDate(now);

        List<com.stratio.casemanagement.model.service.CaseApplication> caseApplications = caseRequest.getCaseApplications();
        if (caseApplications != null) {
            caseApplications.forEach(caseApplication -> {
                caseApplication.setCreationDate(now);
                caseApplication.setModificationDate(now);
            });
        }
    }

    private void setDatesAtModificationTime(CaseRequest caseRequest) {
        LocalDateTime now = LocalDateTime.now();
        caseRequest.setModificationDate(now);

        List<com.stratio.casemanagement.model.service.CaseApplication> caseApplications = caseRequest.getCaseApplications();
        if (caseApplications != null) {
            caseApplications.forEach(caseApplication -> {
                caseApplication.setModificationDate(now);
            });
        }
    }
}
