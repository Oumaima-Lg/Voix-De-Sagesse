package com.voixdesagesse.VoixDeSagesse.service;

import java.util.List;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;

public interface SignalService {

      

    SignalDTO reportArticle(Long reporterId, Long articleId, String reason, String description) throws ArticlaException;

    List<SignalDTO> getPendingSignals() throws ArticlaException;

    List<SignalDTO> getSignalsByReporter(Long reporterId) throws ArticlaException;

    SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId) throws ArticlaException, Exception;

    long getTotalSignals();

    long getPendingSignalsCount();

    List<SignalDTO> getAllSignals() throws ArticlaException;

    void deleteByArticleId(Long id);

    void deleteByReporterId(Long userId);

    void deleteByReportedUserId(Long userId);



}
