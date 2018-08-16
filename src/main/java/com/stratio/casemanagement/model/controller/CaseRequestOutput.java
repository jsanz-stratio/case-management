package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseRequestOutput {
    private Long id;
    private String creationDate;
    private String creationUser;
    private String modificationDate;
    private String modificationUser;
    private String entityId;

    private String caseRawData;
    private String caseParticipant;
}
