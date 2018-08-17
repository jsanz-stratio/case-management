package com.stratio.casemanagement.model.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
}
