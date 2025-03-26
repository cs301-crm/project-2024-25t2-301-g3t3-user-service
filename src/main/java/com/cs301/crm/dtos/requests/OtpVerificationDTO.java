package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OtpVerificationDTO(

        @NotNull(message = "Username cannot be null")
        @NotEmpty(message = "Username cannot be empty")
        String email,

        @NotNull(message = "OTP cannot be null")
        Integer oneTimePassword
) {}
