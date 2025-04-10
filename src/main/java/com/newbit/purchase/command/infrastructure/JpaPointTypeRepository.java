package com.newbit.purchase.command.infrastructure;

import com.newbit.purchase.command.domain.aggregate.PointType;
import com.newbit.purchase.command.domain.repository.PointTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPointTypeRepository extends PointTypeRepository, JpaRepository<PointType, Long> {
}
