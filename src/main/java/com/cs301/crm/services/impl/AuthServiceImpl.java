package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.LoginRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.services.AuthService;
import com.cs301.crm.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GenericResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password())
        );

        if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new InvalidUserCredentials("Username or password is incorrect");
        }

        return new GenericResponseDTO(
                true, jwtUtil.generateToken(userDetails), ZonedDateTime.now()
        );
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return jwtUtil.generateToken(userDetails);
    }
}