package com.newbit.purchase.command.domain.repository;

import com.newbit.purchase.command.domain.aggregate.PointType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointTypeRepository {
    Optional<PointType> findByPointTypeName(String pointTypeName);
    Optional<PointType> findById(long l);
}
