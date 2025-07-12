package com.ecommerce.authservice.mapper;

import com.ecommerce.authservice.dto.RegisterRequest;
import com.ecommerce.authservice.model.Role;
import com.ecommerce.authservice.model.User;
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
