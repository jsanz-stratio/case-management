package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseRequestInput {
    private String operationUser;
    private String entityId;

    private String caseRawData;
    private String caseParticipant;
}
