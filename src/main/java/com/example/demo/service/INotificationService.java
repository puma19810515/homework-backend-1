package com.example.demo.service;

import com.example.demo.req.AddNotificationReq;
import com.example.demo.req.UpdateNotificationReq;
import com.example.demo.res.NotificationRes;
import jakarta.validation.Valid;

import java.util.List;

public interface INotificationService {
    
    NotificationRes add(AddNotificationReq req);

    NotificationRes getById(Long id);

    List<Object> recent();

    NotificationRes update(Long id, UpdateNotificationReq req);

    void delete(Long id);
}
