package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CaseRequestRepository {
    CaseRequest getCaseRequestById(@Param("id") Long id);
    void insertCaseRequest(CaseRequest caseRequest);
    int deleteCaseRequestById(@Param("id") Long id);
    int updateCaseRequestById(@Param("id") Long id, @Param("caseRequest") CaseRequest caseRequest);
}
