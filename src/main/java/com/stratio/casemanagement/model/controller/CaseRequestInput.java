package com.stratio.casemanagement.model.controller;

import lombok.Data;

import java.util.List;

@Data
public class CaseRequestInput {
    private String operationUser;
    private String entityId;

    private String caseRawData;
    private List<CaseRawAttachmentInput> caseRawAttachments;
    private String caseParticipant;
}
