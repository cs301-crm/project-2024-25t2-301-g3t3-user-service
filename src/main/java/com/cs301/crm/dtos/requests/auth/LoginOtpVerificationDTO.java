package com.cs301.crm.dtos.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginOtpVerificationDTO(
        @NotNull(message = "Email cannot be null")
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "OTP cannot be null")
        Integer oneTimePassword
) {}
