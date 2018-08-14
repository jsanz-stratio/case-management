package com.stratio.casemanagement.model.mapper;

public interface SimpleBidirectionalMapper<A, B> extends SimpleMapper<A, B> {
    A mapBToA(B b);
}
