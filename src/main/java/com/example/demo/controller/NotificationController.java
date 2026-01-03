package com.example.demo.controller;

import com.example.demo.req.AddNotificationReq;
import com.example.demo.req.UpdateNotificationReq;
import com.example.demo.res.NotificationRes;
import com.example.demo.service.INotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    @Autowired
    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationRes> create(@Valid @RequestBody AddNotificationReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.add(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationRes> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Object>> recent() {
        return ResponseEntity.ok(notificationService.recent());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationRes> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateNotificationReq req) {
        return ResponseEntity.ok(notificationService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
