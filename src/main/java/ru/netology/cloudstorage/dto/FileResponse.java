package ru.netology.cloudstorage.dto;

import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FileResponse {

    private String filename;

    private Long size;
}