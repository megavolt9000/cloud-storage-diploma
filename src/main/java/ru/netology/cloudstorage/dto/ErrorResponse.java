package ru.netology.cloudstorage.dto;

import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ErrorResponse {

    private String message;

    private Integer id;
}