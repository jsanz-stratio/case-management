package com.stratio.casemanagement.model.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestControllerServiceMapper extends SimpleBidirectionalMapper<
        com.stratio.casemanagement.model.controller.CaseRequest,
        com.stratio.casemanagement.model.service.CaseRequest> {
}
