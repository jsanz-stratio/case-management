package com.stratio.casemanagement.model.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class CaseRequest {
    private Long id;
    private LocalDateTime creationDate;
    private String creationUser;
    private LocalDateTime modificationDate;
    private String modificationUser;
    private String entityId;

    private String caseRawData;
    private List<CaseRawAttachment> caseRawAttachments;
    private String caseParticipant;
    private List<CaseApplication> caseApplications;

    public void addCaseRawAttachment(CaseRawAttachment caseRawAttachment) {
        if (Objects.isNull(caseRawAttachments)) {
            caseRawAttachments = new ArrayList<>();
        }
        caseRawAttachments.add(caseRawAttachment);
    }

    public void addCaseApplication(CaseApplication caseApplication) {
        if (Objects.isNull(caseApplications)) {
            caseApplications = new ArrayList<>();
        }
        caseApplications.add(caseApplication);
    }
}
