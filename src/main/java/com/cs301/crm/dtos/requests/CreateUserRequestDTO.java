package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.*;

public record CreateUserRequestDTO(
        @NotNull(message = "First name cannot be null")
        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotNull(message = "Last name cannot be null")
        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotNull(message = "Email cannot be null")
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, message = "Password must be more than 8 characters")
        @Pattern(message = "Password must contain 1 symbol, 1 uppercase, 1 lowercase and 1 digit",
                regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).*$")
        String password,

        @NotNull(message = "Role cannot be null")
        @NotEmpty(message = "Role cannot be empty")
        String role
) {}