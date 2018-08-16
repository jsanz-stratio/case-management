package com.stratio.casemanagement.model.mapper;

import com.stratio.casemanagement.model.controller.CaseRequestOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestControllerOutboundMapper extends SimpleMapper<
        com.stratio.casemanagement.model.service.CaseRequest,
        CaseRequestOutput> {
}
