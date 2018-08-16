package com.stratio.casemanagement.model.mapper;

import com.stratio.casemanagement.model.controller.CaseRequestInput;
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
    CaseRequest mapForCreate(CaseRequestInput request);

    @Mappings({
            @Mapping(source = "operationUser", target="modificationUser")
    })
    CaseRequest mapForUpdate(CaseRequestInput request);
}
