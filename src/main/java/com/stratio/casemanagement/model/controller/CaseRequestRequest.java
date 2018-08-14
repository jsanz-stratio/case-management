package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseRequestRequest {
    private String creationUser;
    private String modificationUser;
    private String entityId;
}
