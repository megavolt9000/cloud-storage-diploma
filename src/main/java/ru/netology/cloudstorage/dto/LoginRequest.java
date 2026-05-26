package ru.netology.cloudstorage.dto;

import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginRequest {

    private String login;

    private String password;
}