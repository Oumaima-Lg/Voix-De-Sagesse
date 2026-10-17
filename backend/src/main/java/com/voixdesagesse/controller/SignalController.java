package com.voixdesagesse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalReportRequest;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.SignalService;

import lombok.RequiredArgsConstructor;

// Reporting is open to users; reviewing and processing reports is done under /admin.
@RestController
@RequestMapping("/signals")
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping("/report")
    public ResponseEntity<SignalDTO> reportArticle(@RequestBody SignalReportRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) throws VoixDeSagesseException {
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
            throws VoixDeSagesseException {
        return ResponseEntity.ok(signalService.getSignalsByReporter(currentUser.getId()));
    }

}
