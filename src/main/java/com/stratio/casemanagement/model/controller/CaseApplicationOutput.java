package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseApplicationOutput {
    private Long caseId;
    private Long appSeq;
    private String processId;
    private String status;
    private String lockedBy;
    private String creationDate;
    private String creationUser;
    private String modificationDate;
    private String modificationUser;
}
