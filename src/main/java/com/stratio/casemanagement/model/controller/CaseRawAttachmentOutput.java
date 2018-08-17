package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseRawAttachmentOutput {
    private Long caseId;
    private Long seqId;
    private String data;
    private String metadata;
}
