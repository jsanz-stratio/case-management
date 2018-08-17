package com.stratio.casemanagement.model.mapper;

import java.time.LocalDateTime;

public interface BaseMapper {
    default String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toString();
    }
}
