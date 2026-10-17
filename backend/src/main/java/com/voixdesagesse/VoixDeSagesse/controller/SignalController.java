package com.voixdesagesse.VoixDeSagesse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SignalReportRequest;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.jwt.CustomUserDetails;
import com.voixdesagesse.VoixDeSagesse.service.SignalService;

import lombok.RequiredArgsConstructor;

// Reporting is open to users; reviewing and processing reports is done under /admin.
@RestController
@RequestMapping("/signals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SignalController {

    private final SignalService signalService;

    @PostMapping("/report")
    public ResponseEntity<SignalDTO> reportArticle(@RequestBody SignalReportRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws ArticlaException {
        SignalDTO signal = signalService.reportArticle(
            currentUser.getId(),
            request.articleId(),
            request.reason(),
            request.description()
        );
        return ResponseEntity.ok(signal);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<SignalDTO>> getMySignals(@AuthenticationPrincipal CustomUserDetails currentUser)
            throws ArticlaException {
        return ResponseEntity.ok(signalService.getSignalsByReporter(currentUser.getId()));
    }

}
