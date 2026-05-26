package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginResponse {

    @JsonProperty("auth-token")

    private String authToken;
}