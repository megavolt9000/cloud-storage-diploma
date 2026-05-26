package ru.netology.cloudstorage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RenameFileRequest {

    private String filename;
}