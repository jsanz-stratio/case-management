package com.stratio.casemanagement.model.mapper;

import java.time.LocalDateTime;

public interface SimpleBidirectionalMapper<A, B> {
    B mapAToB(A a);

    A mapBToA(B b);

    default String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toString();
    }

    default LocalDateTime stringToLocalDateTime(String string) {
        return LocalDateTime.now();
    }
}
