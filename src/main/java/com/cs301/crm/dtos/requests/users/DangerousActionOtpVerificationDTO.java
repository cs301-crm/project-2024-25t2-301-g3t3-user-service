package com.cs301.crm.dtos.requests.users;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DangerousActionOtpVerificationDTO(
        @NotNull(message = "First name cannot be null")
        @NotEmpty(message = "First name cannot be empty")
        String uuid,

        @NotNull(message = "OTP cannot be null")
        Integer oneTimePassword
) {
}
