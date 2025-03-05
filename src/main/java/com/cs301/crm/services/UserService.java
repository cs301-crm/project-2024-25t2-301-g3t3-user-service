package com.cs301.crm.services;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.dtos.requests.DisableEnableRequestDTO;
import com.cs301.crm.dtos.requests.ResetPasswordRequestDTO;
import com.cs301.crm.dtos.requests.UpdateUserRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;

public interface UserService {
    GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO);
    GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable);
    GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO);
    GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}