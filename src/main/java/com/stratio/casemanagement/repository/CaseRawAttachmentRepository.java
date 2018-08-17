package com.stratio.casemanagement.repository;

import com.stratio.casemanagement.model.repository.CaseRawAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CaseRawAttachmentRepository {
    List<CaseRawAttachment> getCaseRawAttachmenListByCaseId(@Param("caseId") Long caseId);
}
