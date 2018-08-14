package com.stratio.casemanagement.model.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestRequestControllerToCaseRequestServiceMapper extends SimpleMapper<
        com.stratio.casemanagement.model.controller.CaseRequestRequest,
        com.stratio.casemanagement.model.service.CaseRequest> {
}
