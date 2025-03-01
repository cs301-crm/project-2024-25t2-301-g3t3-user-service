package com.cs301.crm.dtos.requests;

public record ResetPasswordRequestDTO(

        String email,

        String oldPassword,

        String newPassword
) {}