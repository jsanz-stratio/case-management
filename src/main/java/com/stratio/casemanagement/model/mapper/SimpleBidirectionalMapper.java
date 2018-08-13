package com.stratio.casemanagement.model.mapper;

import java.time.Instant;

public interface SimpleBidirectionalMapper<A, B> {
    B mapAToB(A a);
    A mapBToA(B b);

    default String instantToString(Instant instant) {
        return instant == null ? null : instant.toString();
    }

    default Instant stringToInstant(String string) {
        // TODO!!!
        return null;
    }
}
