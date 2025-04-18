package com.newbit.newbitfeatureservice.notification.query.service;

import com.newbit.newbitfeatureservice.notification.query.dto.NotificationResponse;
import com.newbit.newbitfeatureservice.notification.query.mapper.NotificationQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryMapper notificationQueryMapper;

    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationQueryMapper.findAllByUserId(userId);
    }
}