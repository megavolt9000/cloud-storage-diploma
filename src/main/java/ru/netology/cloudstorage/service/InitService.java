package ru.netology.cloudstorage.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.repository.UserRepository;

@Service
@RequiredArgsConstructor

public class InitService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        boolean exists = userRepository
                .findByLogin("admin")
                .isPresent();

        if (exists) {
            return;
        }

        UserEntity admin = UserEntity.builder()

                .login("admin")

                .password(
                        passwordEncoder.encode("admin")
                )

                .build();

        userRepository.save(admin);

        System.out.println(
                "Admin user created"
        );
    }
}