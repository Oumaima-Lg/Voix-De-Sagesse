package com.voixdesagesse.service;

import java.util.List;
import java.util.Map;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.dto.UserDTO;
import com.voixdesagesse.exception.VoixDeSagesseException;


public interface AdminService {
    List<UserDTO> getAllUsers() throws VoixDeSagesseException;

    Map<String, Object> getDashboardStats();

    List<SignalDTO> getAllSignals() throws VoixDeSagesseException;

    SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId) throws VoixDeSagesseException, Exception;
    
}
