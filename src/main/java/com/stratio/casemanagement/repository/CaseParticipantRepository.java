package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CaseParticipantRepository {
    CaseParticipant getCaseParticipantById(@Param("caseId") Long caseId);
    void insertCaseParticipant(CaseParticipant caseRawData);
}
