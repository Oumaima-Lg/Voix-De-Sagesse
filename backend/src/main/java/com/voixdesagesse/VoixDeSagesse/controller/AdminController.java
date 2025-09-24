package com.voixdesagesse.VoixDeSagesse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

     @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        log.info("Admin accessing dashboard");
        
        Map<String, Object> stats = adminService.getDashboardStats();
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() throws ArticlaException {
        log.info("Admin fetching all users");
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
   
}