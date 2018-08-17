package com.stratio.casemanagement.model.mapper;

import com.stratio.casemanagement.model.controller.CaseRequestOutput;
import com.stratio.casemanagement.model.service.CaseRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseRequestControllerOutboundMapper extends BaseMapper {
    CaseRequestOutput mapCaseRequestFromService(CaseRequest fromService);
}
