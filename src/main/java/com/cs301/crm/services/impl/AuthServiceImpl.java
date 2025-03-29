package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.auth.LoginOtpVerificationDTO;
import com.cs301.crm.dtos.requests.auth.LoginRequestDTO;
import com.cs301.crm.dtos.requests.auth.ResendOtpRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.producers.KafkaProducer;
import com.cs301.crm.protobuf.Otp;
import com.cs301.crm.services.AuthService;
import com.cs301.crm.utils.JwtUtil;
import com.cs301.crm.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

@Service
public class AuthServiceImpl implements AuthService {
    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserDetailsService userDetailsService,
                           RedisUtil redisUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.redisUtil = redisUtil;
    }

    @Override
    public GenericResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password())
        );

        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new InvalidUserCredentials("Email or password is incorrect");
        }

        redisUtil.generateOtp(loginRequestDTO.email());

        return new GenericResponseDTO(
                true, "Please verify using the OTP sent to your email address", ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO verifyOtp(LoginOtpVerificationDTO otpVerificationDTO) {
        if (redisUtil.verifyOtp(otpVerificationDTO.email(), otpVerificationDTO.oneTimePassword())) {
            throw new InvalidUserCredentials("Wrong OTP, please try again");
        }

        redisUtil.cleanupAfterSuccessfulVerification(otpVerificationDTO.email());

        logger.info("{} login successful", otpVerificationDTO.email());

        return new GenericResponseDTO(
                true, this.generateAccessToken(otpVerificationDTO.email()), ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO resendOtp(ResendOtpRequestDTO otpRequestDTO) {
        redisUtil.invalidateExistingOtps(otpRequestDTO.email());
        redisUtil.generateOtp(otpRequestDTO.email());

        return new GenericResponseDTO(
                true, "A new OTP has been sent to your email address.", ZonedDateTime.now()
        );
    }

    @Override
    public String generateAccessToken(String email) {
        return jwtUtil.generateToken(userDetailsService.loadUserByUsername(email));
    }
}