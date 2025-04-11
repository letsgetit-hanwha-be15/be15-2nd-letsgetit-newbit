package com.newbit.notification.query.repository;

import com.newbit.notification.command.application.dto.response.NotificationSendResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationSendResponse, Long> {
}
