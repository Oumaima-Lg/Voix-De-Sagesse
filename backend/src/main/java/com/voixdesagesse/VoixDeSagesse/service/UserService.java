package com.voixdesagesse.VoixDeSagesse.service;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;


// @Service
public interface UserService {

    public UserDTO registerUser(UserDTO userDTO) throws ArticlaException;

    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException;

}