package com.newbit.newbitfeatureservice.purchase.command.infrastructure;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointType;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.PointTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPointTypeRepository extends PointTypeRepository, JpaRepository<PointType, Long> {
}
