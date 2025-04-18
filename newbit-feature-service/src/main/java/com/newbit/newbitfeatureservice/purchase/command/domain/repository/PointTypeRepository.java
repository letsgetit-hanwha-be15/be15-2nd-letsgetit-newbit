package com.newbit.newbitfeatureservice.purchase.command.domain.repository;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointType;

import java.util.Optional;

public interface PointTypeRepository {
    Optional<PointType> findByPointTypeName(String pointTypeName);
    Optional<PointType> findById(long l);
}
