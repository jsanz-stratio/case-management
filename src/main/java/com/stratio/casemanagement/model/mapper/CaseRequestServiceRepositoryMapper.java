package com.stratio.casemanagement.model.mapper;

import com.stratio.casemanagement.model.service.CaseRawAttachment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CaseRequestServiceRepositoryMapper extends BaseMapper {

    com.stratio.casemanagement.model.service.CaseRequest mapCaseRequestFromRepoToService(com.stratio.casemanagement.model.repository.CaseRequest fromRepo);

    com.stratio.casemanagement.model.repository.CaseRequest mapCaseRequestFromServiceToRepo(com.stratio.casemanagement.model.service.CaseRequest fromService);

    List<CaseRawAttachment> mapRawAttachmentListFromRepoToService(List<com.stratio.casemanagement.model.repository.CaseRawAttachment> fromRepo);
}
