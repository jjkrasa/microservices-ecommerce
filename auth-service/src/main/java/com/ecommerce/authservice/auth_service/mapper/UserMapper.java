package com.ecommerce.authservice.auth_service.mapper;

import com.ecommerce.authservice.auth_service.dto.RegisterRequest;
import com.ecommerce.authservice.auth_service.model.Role;
import com.ecommerce.authservice.auth_service.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Locale;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", expression = "java(toLowerCaseEmail(request.getEmail()))")
    @Mapping(target = "password", ignore = true)
    User registerRequestToUser(RegisterRequest request);

    default String toLowerCaseEmail(String email) {
        return email == null ? null : email.toLowerCase(Locale.ROOT);
    }

    @AfterMapping
    default void setDefaultValues(@MappingTarget User user) {
        user.setRole(Role.USER);
    }
}
