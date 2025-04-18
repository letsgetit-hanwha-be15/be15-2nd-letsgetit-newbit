package com.newbit.notification.command.domain.repository;

import com.newbit.notification.command.domain.aggregate.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
}
