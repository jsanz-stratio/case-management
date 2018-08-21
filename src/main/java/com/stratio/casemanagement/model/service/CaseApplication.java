package com.stratio.casemanagement.model.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseApplication {
    private Long caseId;
    private Long appSeq;
    private String processId;
    private String status;
    private String lockedBy;
    private LocalDateTime creationDate;
    private String creationUser;
    private LocalDateTime modificationDate;
    private String modificationUser;
}
