package com.voixdesagesse.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.dto.UserDTO;
import com.voixdesagesse.exception.VoixDeSagesseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "adminService")
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final ArticleService articleService;
    private final SignalService signalService;

    @Override
    public List<UserDTO> getAllUsers() throws VoixDeSagesseException {
        return userService.getAllUsers();
    }

    @Override
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

    @Override
    public List<SignalDTO> getAllSignals() throws VoixDeSagesseException {
        return signalService.getAllSignals();
    }

    @Override
    public SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId)
            throws VoixDeSagesseException, Exception {
        return signalService.processSignal(signalId, status, adminComment, adminId);
    }
}
