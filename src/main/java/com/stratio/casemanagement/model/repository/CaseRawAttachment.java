package com.stratio.casemanagement.model.repository;

import lombok.Data;

@Data
public class CaseRawAttachment {
    private Long caseId;
    private Long seqId;
    private String data;
    private String metadata;
}
