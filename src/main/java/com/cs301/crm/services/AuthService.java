package com.cs301.crm.services;

import com.cs301.crm.dtos.requests.LoginRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    GenericResponseDTO login(LoginRequestDTO loginRequestDTO);
    String generateAccessToken(UserDetails userDetails);
}