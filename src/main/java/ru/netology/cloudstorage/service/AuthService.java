package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exception.BadCredentialsException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public String login(
            String login,
            String password
    ) {

        UserEntity user = userRepository
                .findByLogin(login)

                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Bad credentials"
                        )
                );

        boolean matches = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        if (!matches) {

            throw new BadCredentialsException(
                    "Bad credentials"
            );
        }

        String token = UUID.randomUUID().toString();

        user.setToken(token);

        userRepository.save(user);

        return token;
    }

    public void logout(
            String authToken
    ) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        UserEntity user = userRepository
                .findByToken(authToken)

                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Unauthorized"
                        )
                );

        user.setToken(null);

        userRepository.save(user);
    }
}
