package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseRawAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CaseRawAttachmentRepository {
    List<CaseRawAttachment> getCaseRawAttachmentListByCaseId(@Param("caseId") Long caseId);
    int insertCaseRawAttachment(CaseRawAttachment caseRawAttachment);
}
