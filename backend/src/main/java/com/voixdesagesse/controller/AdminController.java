package com.voixdesagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.dto.UserDTO;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Administration")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Statistiques du tableau de bord")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        log.info("Admin accessing dashboard");

        Map<String, Object> stats = adminService.getDashboardStats();

        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Liste des utilisateurs")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() throws VoixDeSagesseException {
        log.info("Admin fetching all users");
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Liste des signalements")
    @GetMapping("/signals")
    public ResponseEntity<?> getAllSignals() {
        try {
            log.info("Admin fetching all signals");
            List<SignalDTO> signals = adminService.getAllSignals();
            return ResponseEntity.ok(signals);
        } catch (VoixDeSagesseException e) {
            log.error("Error fetching signals: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Traiter un signalement (statut et commentaire de l'admin)")
    @PutMapping("/signals/{signalId}/process")
    public ResponseEntity<?> processSignal(
            @PathVariable Long signalId,
            @RequestParam SignalStatus status,
            @RequestParam(required = false) String adminComment,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws Exception  {
        try {
            log.info("Admin processing signal: {}", signalId);
            SignalDTO processedSignal = adminService.processSignal(signalId, status, adminComment, currentUser.getId());
            return ResponseEntity.ok(processedSignal);
        } catch (VoixDeSagesseException e) {
            log.error("Error processing signal: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}