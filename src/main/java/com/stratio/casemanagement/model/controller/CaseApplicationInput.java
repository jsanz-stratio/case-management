package com.stratio.casemanagement.model.controller;

import lombok.Data;

@Data
public class CaseApplicationInput {
    private String processId;
    private String status;
    private String lockedBy;
}
