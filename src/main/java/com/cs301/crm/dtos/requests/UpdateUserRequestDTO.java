package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.*;

public record UpdateUserRequestDTO(
        @NotNull(message = "User Id cannot be null")
        @NotEmpty(message = "User Id cannot be empty")
        String userId,

        @NotNull(message = "First name cannot be null")
        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotNull(message = "Last name cannot be null")
        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotNull(message = "Email cannot be null")
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
)
{}
