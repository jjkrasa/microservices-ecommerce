package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.LoginRequest;
import com.ecommerce.authservice.dto.RegisterRequest;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.model.Role;
import com.ecommerce.authservice.model.User;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.ConflictException;
import com.ecommerce.exceptionlib.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private RegisterRequest registerRequest;

    private LoginRequest loginRequest;

    private User user;

    @BeforeEach
    void setUp() {
        String email = "email@mail.com";

        registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword("Password123!");
        registerRequest.setConfirmPassword("Password123!");

        loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("Password123!");

        user = User.builder()
                .id(1L)
                .email(email)
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    @Test
    public void register_shouldThrowBadRequest_whenPasswordsDoNotMatch() {
        registerRequest.setPassword("Password123");

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
    }

    @Test
    public void register_shouldThrowConflict_whenEmailIsUsed() {
        when(userRepository.findByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
    }

    @Test
    public void register_successful() {
        when(userRepository.findByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.registerRequestToUser(registerRequest)).thenReturn(user);

        authService.register(registerRequest);

        verify(userRepository).findByEmailIgnoreCase(registerRequest.getEmail());
        verify(userMapper).registerRequestToUser(registerRequest);
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void login_shouldThrowUnauthorized_whenEmailNotFound() {
        when(userRepository.findByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    public void login_shouldThrowUnauthorized_whenPasswordsDontMatch() {
        when(userRepository.findByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    public void login_successful() {
        when(userRepository.findByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("JWT");

        String token = authService.login(loginRequest);

        assertEquals("JWT", token);
    }
}