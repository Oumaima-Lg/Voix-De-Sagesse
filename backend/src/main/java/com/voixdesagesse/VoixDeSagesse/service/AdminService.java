package com.voixdesagesse.VoixDeSagesse.service;

import java.util.List;
import java.util.Map;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;


public interface AdminService {
    List<UserDTO> getAllUsers() throws ArticlaException;

    Map<String, Object> getDashboardStats();

    List<SignalDTO> getAllSignals() throws ArticlaException;

    SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId) throws ArticlaException, Exception;
    
}
