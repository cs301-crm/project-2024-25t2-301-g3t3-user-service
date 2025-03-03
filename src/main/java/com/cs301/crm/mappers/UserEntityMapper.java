package com.cs301.crm.mappers;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.models.User;
import com.cs301.crm.models.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "userRole", target = "role", qualifiedByName = "userRoleToRole")
    @Mapping(source = "password", target = "password", ignore = true)
    GenericResponseDTO userToGenericResponseDTO(User user);

    @Mapping(source = "role", target = "userRole", qualifiedByName = "roleToUserRole")
    User createUserRequestDTOtoUser(CreateUserRequestDTO createUserRequestDTO);

    User 

    @Named("roleToUserRole")
    default UserRole stringToUserType(String role) {
        return UserRole.valueOf(role.toUpperCase()); // Matches case-insensitively
    }

    @Named("userRoleToRole")
    default String userTypeToString(UserRole role) {
        return role.toString();
    }
}
