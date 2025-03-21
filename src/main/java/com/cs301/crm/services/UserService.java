package com.cs301.crm.services;

import com.cs301.crm.dtos.requests.*;
import com.cs301.crm.dtos.responses.GenericResponseDTO;

import java.util.concurrent.ExecutionException;

public interface UserService {
    GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO);
    GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable);
    GenericResponseDTO enableUser(OtpVerificationDTO otpVerificationDTO) throws ExecutionException;
    GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO);
    GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}