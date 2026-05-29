package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exception.BadCredentialsException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;

    @BeforeEach
    void setUp() {

        user = UserEntity.builder()
                .id(1L)
                .login("user")
                .password("encodedPassword")
                .build();
    }

    @Test
    void loginSuccess() {

        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("password")
                .build();

        when(userRepository.findByLogin("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"
        )).thenReturn(true);

        String token = authService.login(
                request.getLogin(),
                request.getPassword()
        );

        assertNotNull(token);

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void loginBadCredentialsWhenUserNotFound() {

        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("password")
                .build();

        when(userRepository.findByLogin("user"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(
                        request.getLogin(),
                        request.getPassword()
                )
        );
    }

    @Test
    void loginBadCredentialsWhenPasswordWrong() {

        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("wrong")
                .build();

        when(userRepository.findByLogin("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(
                        request.getLogin(),
                        request.getPassword()
                )
        );
    }

    @Test
    void logoutSuccess() {

        user.setToken("token");

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        authService.logout("token");

        assertNull(user.getToken());

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void logoutUnauthorized() {

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedException.class,
                () -> authService.logout("token")
        );
    }
}
