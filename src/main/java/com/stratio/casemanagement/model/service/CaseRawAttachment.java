package com.stratio.casemanagement.model.service;

import lombok.Data;

@Data
public class CaseRawAttachment {
    private Long caseId;
    private Long seqId;
    private String data;
    private String metadata;
}
