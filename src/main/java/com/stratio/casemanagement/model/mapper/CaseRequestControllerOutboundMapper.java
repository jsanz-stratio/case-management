package com.stratio.casemanagement.model.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestControllerOutboundMapper extends SimpleMapper<
        com.stratio.casemanagement.model.service.CaseRequest,
        com.stratio.casemanagement.model.controller.CaseRequestResponse> {
}
