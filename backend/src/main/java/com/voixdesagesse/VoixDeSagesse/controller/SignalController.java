package com.voixdesagesse.VoixDeSagesse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SignalReportRequest;
import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.service.SignalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/signals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SignalController {
    
    private final SignalService signalService;

    @PostMapping("/report")
    public ResponseEntity<SignalDTO> reportArticle(@RequestBody SignalReportRequest request) {
        try {
            SignalDTO signal = signalService.reportArticle(
                request.reporterId(), 
                request.articleId(), 
                request.reason(), 
                request.description()
            );
            return ResponseEntity.ok(signal);
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<SignalDTO>> getPendingSignals() {
        try {
            List<SignalDTO> signals = signalService.getPendingSignals();
            return ResponseEntity.ok(signals);
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-reporter/{reporterId}")
    public ResponseEntity<List<SignalDTO>> getSignalsByReporter(@PathVariable Long reporterId) {
        try {
            List<SignalDTO> signals = signalService.getSignalsByReporter(reporterId);
            return ResponseEntity.ok(signals);
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/process/{signalId}")
    public ResponseEntity<SignalDTO> processSignal(
            @PathVariable Long signalId,
            @RequestParam SignalStatus status,
            @RequestParam Long adminId,
            @RequestParam(required = false) String adminComment) throws Exception {
        try {
            SignalDTO signal = signalService.processSignal(signalId, status, adminComment, adminId);
            return ResponseEntity.ok(signal);
        } catch (ArticlaException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}