package com.cs301.crm.services;

import com.cs301.crm.dtos.requests.auth.LoginOtpVerificationDTO;
import com.cs301.crm.dtos.requests.auth.LoginRequestDTO;
import com.cs301.crm.dtos.requests.auth.ResendOtpRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;

import java.util.concurrent.ExecutionException;

public interface AuthService {
    GenericResponseDTO login(LoginRequestDTO loginRequestDTO);
    GenericResponseDTO verifyOtp(LoginOtpVerificationDTO otpVerificationDTO) throws ExecutionException;
    GenericResponseDTO resendOtp(ResendOtpRequestDTO otpRequestDTO) throws ExecutionException;
    String generateAccessToken(String email);
}