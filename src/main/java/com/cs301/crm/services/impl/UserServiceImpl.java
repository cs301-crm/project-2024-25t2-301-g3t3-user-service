package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.*;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.exceptions.InvalidOtpException;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.mappers.UserEntityMapper;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.models.UserRole;
import com.cs301.crm.producers.KafkaProducer;
import com.cs301.crm.protobuf.Notification;
import com.cs301.crm.protobuf.Otp;
import com.cs301.crm.repositories.UserRepository;
import com.cs301.crm.services.UserService;
import com.cs301.crm.utils.PasswordUtil;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoadingCache<String, Integer> oneTimePasswordCache;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoadingCache<String, Integer> oneTimePasswordCache,
                           KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.oneTimePasswordCache = oneTimePasswordCache;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional
    public GenericResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        // Create the new user
        String tempPassword = PasswordUtil.generatePassword();
        UserEntity userEntity = UserEntityMapper.INSTANCE.createUserRequestDTOtoUserEntity(createUserRequestDTO);
        userEntity.setEnabled(true);
        userEntity.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(userEntity);
        logger.info("Saved {} into the database with generated password {}", userEntity, tempPassword);

        // Generate otp for current user
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String actorId = jwt.getClaimAsString("sub");

        oneTimePasswordCache.invalidate(actorId);
        final int otp = PasswordUtil.generateOtp();
        oneTimePasswordCache.put(actorId, otp);
        logger.info("Generated {} for {}", otp, actorId);

        // Send otp message to kafka
        UserEntity actor = userRepository.findById(UUID.fromString(actorId))
                .orElseThrow(() -> new UsernameNotFoundException(actorId));

        Otp otpMessage = Otp.newBuilder()
                .setEmail(actor.getEmail())
                .setOtp(otp)
                .setTimestamp(Instant.now().toString())
                .build();

        kafkaProducer.produceMessage(otpMessage);
        logger.info("Sent otp message to Kafka");

        // Send notification of account creation to new user
        Notification notificationMessage = Notification.newBuilder()
                .setEmail(userEntity.getEmail())
                .setUsername(userEntity.getFirstName())
                .setTempPassword(tempPassword)
                .setRole(userEntity.getUserRole().toString())
                .build();

        kafkaProducer.produceMessage(notificationMessage);
        logger.info("Sent notification message to Kafka");
        return new GenericResponseDTO(
                true, "User saved successfully, please verify account creation with OTP sent to the email", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO toggleEnable(DisableEnableRequestDTO disableEnableRequestDTO, boolean enable) {
        UserEntity userEntity = userRepository.findByEmail(disableEnableRequestDTO.email()).orElseThrow(
                () -> new UsernameNotFoundException(disableEnableRequestDTO.email())
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
        String username = otpVerificationDTO.email();
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );

        Integer storedOneTimePassword = oneTimePasswordCache.get(username);

        if (!storedOneTimePassword.equals(otpVerificationDTO.oneTimePassword())) {
            throw new InvalidOtpException("OTP value is wrong. Please try again");
        }

        userEntity.setEmailVerified(true);
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

        logger.info("2FA passed, enabled {}", userEntity);

        return new GenericResponseDTO(
                true, "2FA verification successful", ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    public GenericResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        UserEntity oldUserEntity = userRepository.findByEmail(updateUserRequestDTO.email()).orElseThrow(
                () -> new UsernameNotFoundException(updateUserRequestDTO.email())
        );
        oldUserEntity.setFirstName(updateUserRequestDTO.firstName());
        oldUserEntity.setLastName(updateUserRequestDTO.lastName());
        oldUserEntity.setEmail(updateUserRequestDTO.email());
        oldUserEntity.setUserRole(UserRole.valueOf(updateUserRequestDTO.userRole()));

        userRepository.save(oldUserEntity);

        return new GenericResponseDTO(
                true, "User updated successfully", ZonedDateTime.now()
        );
    }

    @Override
    public GenericResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        UserEntity userEntity = userRepository.findByEmail(resetPasswordRequestDTO.email()).orElseThrow(
                () -> new UsernameNotFoundException(resetPasswordRequestDTO.email())
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