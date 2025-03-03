package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DisableEnableRequestDTO(
        @NotNull(message = "Username cannot be null")
        @NotEmpty(message = "Username cannot be empty")
        String username
) {
}
