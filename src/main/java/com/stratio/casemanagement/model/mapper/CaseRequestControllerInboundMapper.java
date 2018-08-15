package com.stratio.casemanagement.model.mapper;

import com.stratio.casemanagement.model.controller.CaseRequestRequest;
import com.stratio.casemanagement.model.service.CaseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CaseRequestControllerInboundMapper {
    @Mappings({
            @Mapping(source = "operationUser", target="creationUser"),
            @Mapping(source = "operationUser", target="modificationUser")
    })
    CaseRequest mapForCreate(CaseRequestRequest request);

    @Mappings({
            @Mapping(source = "operationUser", target="modificationUser")
    })
    CaseRequest mapForUpdate(CaseRequestRequest request);
}
