package com.manish.pragyan.modules.auth.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.manish.pragyan.modules.auth.dtos.LoginResponseDto;
import com.manish.pragyan.modules.auth.dtos.RegisterRequestDto;
import com.manish.pragyan.modules.auth.dtos.RegisterResponseDto;
import com.manish.pragyan.modules.auth.entities.CustomUserDetails;
import com.manish.pragyan.modules.auth.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    // Register Request DTO to Entity
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity registerRequestToEntity(RegisterRequestDto registerRequestDto);

    // CustomUserDetails to Regsiter Response DTO
    @Mapping(target = "user.name", source = "customUserDetails.name")
    @Mapping(target = "user.email", source = "customUserDetails.email")
    @Mapping(target = "user.password", source = "customUserDetails.password")
    @Mapping(target = "user.role", source = "customUserDetails.role")
    @Mapping(target = "user.provider", source = "customUserDetails.provider")
    LoginResponseDto customUserDetailsToRegisterResponseDto(CustomUserDetails customUserDetails, String token);

    // Entity to Register Response DTO
    RegisterResponseDto entityToRegisterResponseDto(UserEntity userEntity);

}
