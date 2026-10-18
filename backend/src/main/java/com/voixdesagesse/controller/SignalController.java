package com.voixdesagesse.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalReportRequest;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.security.CustomUserDetails;
import com.voixdesagesse.service.SignalService;

import lombok.RequiredArgsConstructor;

// Reporting is open to users; reviewing and processing reports is done under /admin.
@Tag(name = "Signalements")
@RestController
@RequestMapping("/signals")
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @Operation(summary = "Signaler un article")
    @PostMapping
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

}
