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
import com.cs301.crm.utils.OtpUtil;
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
    private final OtpUtil otpUtil;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserDetailsService userDetailsService,
                           OtpUtil otpUtil,
                           KafkaProducer kafkaProducer) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.otpUtil = otpUtil;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public GenericResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password())
        );

        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new InvalidUserCredentials("Email or password is incorrect");
        }

        sendOtp(loginRequestDTO.email());

        return new GenericResponseDTO(
                true, "Please verify using the OTP sent to your email address", ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO verifyOtp(LoginOtpVerificationDTO otpVerificationDTO) throws ExecutionException {
        if (otpUtil.verifyOtp(otpVerificationDTO.email(), otpVerificationDTO.oneTimePassword())) {
            throw new InvalidUserCredentials("Wrong OTP, please try again");
        }

        otpUtil.invalidateOtp(otpVerificationDTO.email());

        logger.info("{} login successful", otpVerificationDTO.email());

        return new GenericResponseDTO(
                true, this.generateAccessToken(otpVerificationDTO.email()), ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO resendOtp(ResendOtpRequestDTO otpRequestDTO) {
        otpUtil.invalidateOtp(otpRequestDTO.email());
        sendOtp(otpRequestDTO.email());

        return new GenericResponseDTO(
                true, "A new OTP has been sent to your email address.", ZonedDateTime.now()
        );
    }

    @Override
    public String generateAccessToken(String email) {
        return jwtUtil.generateToken(userDetailsService.loadUserByUsername(email));
    }

    private void sendOtp(String email) {
        final int otp = otpUtil.generateOtp(email);

        Otp otpMessage = Otp.newBuilder()
                .setEmail(email)
                .setOtp(otp)
                .setTimestamp(Instant.now().toString())
                .build();

        kafkaProducer.produceMessage(otpMessage);
        logger.info("Otp message sent to Kafka from auth service");
    }
}