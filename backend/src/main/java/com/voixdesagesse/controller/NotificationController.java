package com.voixdesagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.NotificationDTO;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Notifications")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Ses 50 dernières notifications, les plus récentes d'abord")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(notificationService.getLatest(currentUser.getId()));
    }

    @Operation(summary = "Nombre de notifications non lues")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(currentUser.getId())));
    }

    @Operation(summary = "Marquer toutes ses notifications comme lues")
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAllRead(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
