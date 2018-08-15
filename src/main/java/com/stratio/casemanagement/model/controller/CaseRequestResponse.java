package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseRequestResponse {
    private Long id;
    private String creationDate;
    private String creationUser;
    private String modificationDate;
    private String modificationUser;
    private String entityId;
}