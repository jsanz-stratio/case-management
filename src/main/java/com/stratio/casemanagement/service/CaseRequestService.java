package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.service.CaseRequest;

public interface CaseRequestService {
    int deleteCaseRequestById(Long id);

    CaseRequest getCaseRequestById(Long id);

    CaseRequest insertCaseRequest(CaseRequest caseRequest);

    int updateCaseRequestById(Long id, CaseRequest caseRequest);
}
