package com.example.demo.controller;

import com.example.demo.enums.NotificationType;
import com.example.demo.req.AddNotificationReq;
import com.example.demo.req.UpdateNotificationReq;
import com.example.demo.res.NotificationRes;
import com.example.demo.service.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        AddNotificationReq req = new AddNotificationReq();
        // 模擬填入資料
        req.setContent("Hello World");
        req.setSubject("Subject");
        req.setType(NotificationType.email);
        NotificationRes res = new NotificationRes();
        res.setId(1L);

        res.setContent(req.getContent());

        when(notificationService.add(req)).thenReturn(res);

        ResponseEntity<NotificationRes> response = notificationController.create(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(res, response.getBody());
        verify(notificationService, times(1)).add(req);
    }

    @Test
    void testGetById() {
        Long id = 1L;
        NotificationRes res = new NotificationRes();
        res.setId(id);

        when(notificationService.getById(id)).thenReturn(res);

        ResponseEntity<NotificationRes> response = notificationController.getById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(res, response.getBody());
        verify(notificationService, times(1)).getById(id);
    }

    @Test
    void testRecent() {
        List<Object> recentList = Arrays.asList("Notif1", "Notif2");
        when(notificationService.recent()).thenReturn(recentList);

        ResponseEntity<List<Object>> response = notificationController.recent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recentList, response.getBody());
        verify(notificationService, times(1)).recent();
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        UpdateNotificationReq req = new UpdateNotificationReq();
        req.setContent("Updated Content");
        req.setSubject("Updated Subject");

        NotificationRes res = new NotificationRes();
        res.setId(id);
        res.setContent(req.getContent());
        res.setSubject(req.getSubject());

        when(notificationService.update(id, req)).thenReturn(res);

        ResponseEntity<NotificationRes> response = notificationController.update(id, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(res, response.getBody());
        verify(notificationService, times(1)).update(id, req);
    }

    @Test
    void testDelete() {
        Long id = 1L;

        // delete 是 void 方法，只需要驗證被呼叫
        doNothing().when(notificationService).delete(id);

        ResponseEntity<Void> response = notificationController.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(notificationService, times(1)).delete(id);
    }
}
