package com.cs301.crm.mappers;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.models.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {
    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);

    UserEntity createUserRequestDTOtoUserEntity(CreateUserRequestDTO createUserRequestDTO);

}
