package com.stratio.casemanagement.model.service;

import lombok.Data;

import java.time.Instant;

@Data
public class CaseRequest {
    private Long id;
    private Instant creationDate;
    private String creationUser;
    private Instant modificationDate;
    private String modificationUser;
    private String entityId;
}
