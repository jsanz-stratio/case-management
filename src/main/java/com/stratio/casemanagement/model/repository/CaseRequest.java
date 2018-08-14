package com.stratio.casemanagement.model.repository;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseRequest {
    private Long id;
    private LocalDateTime creationDate;
    private String creationUser;
    private LocalDateTime modificationDate;
    private String modificationUser;
    private String entityId;
}
