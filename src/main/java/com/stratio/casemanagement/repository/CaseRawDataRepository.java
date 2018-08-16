package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseRawData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaseRawDataRepository {
    void insertCaseRawData(CaseRawData caseRawData);
}
