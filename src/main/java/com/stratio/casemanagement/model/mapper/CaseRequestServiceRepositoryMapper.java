package com.stratio.casemanagement.model.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestServiceRepositoryMapper extends SimpleBidirectionalMapper<
        com.stratio.casemanagement.model.service.CaseRequest,
        com.stratio.casemanagement.model.repository.CaseRequest> {
}
