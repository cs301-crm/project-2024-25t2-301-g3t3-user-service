package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.dtos.requests.DisableEnableRequestDTO;
import com.cs301.crm.dtos.requests.ResetPasswordRequestDTO;
import com.cs301.crm.dtos.requests.UpdateUserRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.mappers.UserEntityMapper;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.models.UserRole;
import com.cs301.crm.repositories.UserRepository;
import com.cs301.crm.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper userEntityMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserEntityMapper userEntityMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        UserEntity userEntity = userEntityMapper.createUserRequestDTOtoUserEntity(createUserRequestDTO);
        userEntity.setEnabled(true);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return new GenericResponseDTO(
                true, "User saved successfully", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable) {
        UserEntity userEntity = userRepository.findByUsername(disableEnableRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException("User not found" + disableEnableRequestDTO.username())
        );
        userEntity.setEnabled(enable);
        userRepository.save(userEntity);

        String result = enable ? "enabled" : "disabled";
        return new GenericResponseDTO(
                true, "User " + result + " successfully", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        UserEntity oldUserEntity = userRepository.findByUsername(updateUserRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException("User not found" + updateUserRequestDTO.username())
        );
        oldUserEntity.setFirstName(updateUserRequestDTO.firstName());
        oldUserEntity.setLastName(updateUserRequestDTO.lastName());
        oldUserEntity.setUsername(updateUserRequestDTO.username());
        oldUserEntity.setEmail(updateUserRequestDTO.email());
        oldUserEntity.setUserRole(UserRole.valueOf(updateUserRequestDTO.role()));

        userRepository.save(oldUserEntity);

        return new GenericResponseDTO(
                true, "User updated successfully", ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        UserEntity userEntity = userRepository.findByUsername(resetPasswordRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException("User not found" + resetPasswordRequestDTO.username())
        );

        if (!passwordEncoder.matches(
                resetPasswordRequestDTO.oldPassword(), userEntity.getPassword())
        ) {
            throw new InvalidUserCredentials("Invalid old password");
        }
        userEntity.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.newPassword()));

        return new GenericResponseDTO(
                true, "User password updated successfully", ZonedDateTime.now()
        );
    }
}