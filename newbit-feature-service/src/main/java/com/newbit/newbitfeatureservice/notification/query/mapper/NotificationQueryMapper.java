package com.newbit.newbitfeatureservice.notification.query.mapper;

import com.newbit.newbitfeatureservice.notification.query.dto.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationQueryMapper {
    List<NotificationResponse> findAllByUserId(Long userId);
}