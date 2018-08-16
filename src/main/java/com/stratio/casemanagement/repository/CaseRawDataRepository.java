package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseRawData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CaseRawDataRepository {
    void insertCaseRawData(CaseRawData caseRawData);
    CaseRawData getCaseRawDataById(@Param("caseId") Long caseId);
}
