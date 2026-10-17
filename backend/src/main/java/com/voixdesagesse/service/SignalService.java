package com.voixdesagesse.service;

import java.util.List;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.dto.SignalStatus;

public interface SignalService {

      

    SignalDTO reportArticle(Long reporterId, Long articleId, String reason, String description) throws VoixDeSagesseException;

    List<SignalDTO> getPendingSignals() throws VoixDeSagesseException;

    List<SignalDTO> getSignalsByReporter(Long reporterId) throws VoixDeSagesseException;

    SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId) throws VoixDeSagesseException, Exception;

    long getTotalSignals();

    long getPendingSignalsCount();

    List<SignalDTO> getAllSignals() throws VoixDeSagesseException;

    void deleteByArticleId(Long id);

    void deleteByReporterId(Long userId);

    void deleteByReportedUserId(Long userId);



}
