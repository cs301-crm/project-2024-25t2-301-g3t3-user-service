package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.*;

public record ResetPasswordRequestDTO(
        @NotNull(message = "Username cannot be null")
        @NotEmpty(message = "Username cannot be empty")
        String username,

        @NotNull(message = "Password cannot be null")
        @NotEmpty(message = "Password cannot be empty")
        String oldPassword,

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, message = "Password must be more than 8 characters")
        @Pattern(message = "Password must contain 1 symbol, 1 uppercase, 1 lowercase and 1 digit",
                regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).*$")
        String newPassword
) {}