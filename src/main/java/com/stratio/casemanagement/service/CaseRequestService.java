package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.service.CaseRequest;

public interface CaseRequestService {
    CaseRequest getCaseRequestById(Long id);
    CaseRequest insertCaseRequest(CaseRequest caseRequest);
    int deleteCaseRequestById(Long id);
}
