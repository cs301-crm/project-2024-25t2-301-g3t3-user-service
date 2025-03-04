package com.cs301.crm.dtos.requests;

import jakarta.validation.constraints.*;

public record UpdateUserRequestDTO(

        @NotNull(message = "First name cannot be null")
        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotNull(message = "Last name cannot be null")
        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotNull(message = "Username cannot be null")
        @NotEmpty(message = "Username cannot be empty")
        String username,

        @NotNull(message = "Email cannot be null")
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role cannot be null")
        @NotEmpty(message = "Role cannot be empty")
        String userRole
)
{}
