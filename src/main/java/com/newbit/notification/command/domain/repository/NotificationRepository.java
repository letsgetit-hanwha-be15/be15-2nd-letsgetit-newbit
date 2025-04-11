package com.newbit.notification.command.domain.repository;

import com.newbit.notification.command.domain.aggregate.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Collection<Object> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
