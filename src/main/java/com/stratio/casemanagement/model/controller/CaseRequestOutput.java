package com.stratio.casemanagement.model.controller;

import lombok.Data;

import java.util.List;

@Data
public class CaseRequestOutput {
    private Long id;
    private String creationDate;
    private String creationUser;
    private String modificationDate;
    private String modificationUser;
    private String entityId;

    private String caseRawData;
    private List<CaseRawAttachmentOutput> caseRawAttachments;
    private String caseParticipant;
    private List<CaseApplicationOutput> caseApplications;
}
