package com.cs301.crm.dtos.requests;

public record CreateUserRequestDTO(
        String firstName,

        String lastName,

        String email,

        String password,

        String role
) {}