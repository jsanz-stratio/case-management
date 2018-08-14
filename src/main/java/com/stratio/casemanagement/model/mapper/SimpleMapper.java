package com.stratio.casemanagement.model.mapper;

import java.time.LocalDateTime;

public interface SimpleMapper<A, B> {
    B mapAToB(A a);

    default String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toString();
    }
}
