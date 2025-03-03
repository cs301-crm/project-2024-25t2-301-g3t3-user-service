package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
        @NotNull(message = "Username cannot be null")
        @NotEmpty(message = "Username cannot be empty")
        String username,

        @NotNull(message = "Password cannot be null")
        @NotEmpty(message = "Password cannot be empty")
        String password
) {}