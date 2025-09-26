package com.voixdesagesse.VoixDeSagesse.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final ArticleService articleService;
    private final SignalService signalService;

    public List<UserDTO> getAllUsers() throws ArticlaException {
        return userService.getAllUsers();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("totalArticles", articleService.getTotalArticles());
        stats.put("newUsersToday", userService.getNewUsersToday());
        stats.put("newArticlesToday", articleService.getNewArticlesToday());

        stats.put("totalSignals", signalService.getTotalSignals());
        stats.put("pendingSignals", signalService.getPendingSignalsCount());

        return stats;
    }

    public List<SignalDTO> getAllSignals() throws ArticlaException {
        return signalService.getAllSignals();
    }

    public SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId)
            throws ArticlaException {
        return signalService.processSignal(signalId, status, adminComment, adminId);
    }
}
