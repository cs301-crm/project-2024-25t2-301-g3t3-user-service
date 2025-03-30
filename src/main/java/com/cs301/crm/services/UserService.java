package com.cs301.crm.services;

import com.cs301.crm.dtos.requests.*;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.models.OtpContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {
    GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) throws JsonProcessingException;
    GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable) throws JsonProcessingException;
    GenericResponseDTO verifyOtp(OtpVerificationDTO otpVerificationDTO, String otpContext) throws JsonProcessingException;
    GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) throws JsonProcessingException;
    GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}