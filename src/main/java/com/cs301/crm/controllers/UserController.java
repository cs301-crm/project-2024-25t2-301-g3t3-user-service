package com.cs301.crm.controllers;

import com.cs301.crm.dtos.requests.*;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<GenericResponseDTO> create(
            @RequestBody @Valid CreateUserRequestDTO createUserRequestDTO
    ) throws JsonProcessingException {
        return new ResponseEntity<>(userService.createUser(createUserRequestDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/disable")
    public ResponseEntity<GenericResponseDTO> disable(
            @RequestBody @Valid DisableEnableRequestDTO disableEnableRequestDTO
    ) throws JsonProcessingException {
        return ResponseEntity.ok(userService.toggleEnable(disableEnableRequestDTO, false));
    }

    @PatchMapping("/enable")
    public ResponseEntity<GenericResponseDTO> enable(
            @RequestBody @Valid DisableEnableRequestDTO disableEnableRequestDTO
    ) throws JsonProcessingException {
        return ResponseEntity.ok(userService.toggleEnable(disableEnableRequestDTO, true));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponseDTO> verifyOtp(
            @RequestBody @Valid DangerousActionOtpVerificationDTO otpVerificationDTO
    ) throws JsonProcessingException {
        return ResponseEntity.ok(userService.verifyOtp(otpVerificationDTO));
    }


    @PutMapping
    public ResponseEntity<GenericResponseDTO> update(
            @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO
    ) throws JsonProcessingException {
        return ResponseEntity.ok(userService.updateUser(updateUserRequestDTO));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponseDTO> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO
    ) {
        return ResponseEntity.ok(userService.resetPassword(resetPasswordRequestDTO));
    }
}
