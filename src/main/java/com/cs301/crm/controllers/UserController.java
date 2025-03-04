package com.cs301.crm.controllers;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.dtos.requests.DisableEnableRequestDTO;
import com.cs301.crm.dtos.requests.ResetPasswordRequestDTO;
import com.cs301.crm.dtos.requests.UpdateUserRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<GenericResponseDTO> create(@RequestBody @Valid CreateUserRequestDTO createUserRequestDTO) {
        return new ResponseEntity<>(userService.createUser(createUserRequestDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/disable")
    public ResponseEntity<GenericResponseDTO> disable(
            @RequestBody @Valid DisableEnableRequestDTO disableEnableRequestDTO
    ) {
        return ResponseEntity.ok(userService.toggleEnable(disableEnableRequestDTO, false));
    }

    @PatchMapping("/enable")
    public ResponseEntity<GenericResponseDTO> enable(
            @RequestBody @Valid DisableEnableRequestDTO disableEnableRequestDTO
    ) {
        return ResponseEntity.ok(userService.toggleEnable(disableEnableRequestDTO, true));
    }

    @PutMapping
    public ResponseEntity<GenericResponseDTO> update(
            @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(updateUserRequestDTO));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponseDTO> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO
    ) {
        return ResponseEntity.ok(userService.resetPassword(resetPasswordRequestDTO));
    }
}
