package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// TODO: Logger!
public class CaseRequestServiceDefault implements CaseRequestService {

    private final CaseRequestRepository caseRequestRepository;
    private final CaseRequestServiceRepositoryMapper mapper;

    @Autowired
    public CaseRequestServiceDefault(CaseRequestRepository caseRequestRepository, CaseRequestServiceRepositoryMapper mapper) {
        this.caseRequestRepository = caseRequestRepository;
        this.mapper = mapper;
    }

    @Override
    public CaseRequest getCaseRequestById(Long id) {
        return mapper.mapBToA(caseRequestRepository.getCaseRequestById(id));
    }
}
