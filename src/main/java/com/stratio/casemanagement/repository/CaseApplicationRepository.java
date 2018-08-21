package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CaseApplicationRepository {
    List<CaseApplication> getCaseApplicationListByCaseId(@Param("caseId") Long caseId);
    int insertCaseApplication(CaseApplication caseApplication);
}
