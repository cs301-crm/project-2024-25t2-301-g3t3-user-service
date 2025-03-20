package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.*;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.mappers.UserEntityMapper;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.models.UserRole;
import com.cs301.crm.repositories.UserRepository;
import com.cs301.crm.services.UserService;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoadingCache<String, Integer> oneTimePasswordCache;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoadingCache<String, Integer> oneTimePasswordCache) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.oneTimePasswordCache = oneTimePasswordCache;
    }

    @Override
    @Transactional
    public GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        String username = createUserRequestDTO.username();
        UserEntity userEntity = UserEntityMapper.INSTANCE.createUserRequestDTOtoUserEntity(createUserRequestDTO);
        userEntity.setEnabled(false);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);

        oneTimePasswordCache.invalidate(username);
        final int otp = new Random().ints(1, 100000, 999999).sum();
        oneTimePasswordCache.put(username, otp);

        return new GenericResponseDTO(
                true, "User saved successfully, please verify account creation with OTP sent to the email", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable) {
        UserEntity userEntity = userRepository.findByUsername(disableEnableRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException(disableEnableRequestDTO.username())
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
    public GenericResponseDTO enableUser(OtpVerificationDTO otpVerificationDTO) throws ExecutionException {
        String username = otpVerificationDTO.username();
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );

        Integer storedOneTimePassword = oneTimePasswordCache.get(username);

        if (!storedOneTimePassword.equals(otpVerificationDTO.oneTimePassword())) {
            return new GenericResponseDTO(
                    false, "2FA verification unsuccessful", ZonedDateTime.now()
            );
        }
        userEntity.setEmailVerified(true);
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

        return new GenericResponseDTO(
                true, "2FA verification successful", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        UserEntity oldUserEntity = userRepository.findByUsername(updateUserRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException(updateUserRequestDTO.username())
        );
        oldUserEntity.setFirstName(updateUserRequestDTO.firstName());
        oldUserEntity.setLastName(updateUserRequestDTO.lastName());
        oldUserEntity.setUsername(updateUserRequestDTO.username());
        oldUserEntity.setEmail(updateUserRequestDTO.email());
        oldUserEntity.setUserRole(UserRole.valueOf(updateUserRequestDTO.userRole()));

        userRepository.save(oldUserEntity);

        return new GenericResponseDTO(
                true, "User updated successfully", ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        UserEntity userEntity = userRepository.findByUsername(resetPasswordRequestDTO.username()).orElseThrow(
                () -> new UsernameNotFoundException(resetPasswordRequestDTO.username())
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