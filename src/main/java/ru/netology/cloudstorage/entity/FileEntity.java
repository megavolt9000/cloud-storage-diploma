package ru.netology.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)

    private String filename;

    @Column(name = "file_size", nullable = false)

    private Long fileSize;

    @Column(nullable = false)

    private String path;

    @Column(name = "upload_date")

    private LocalDateTime uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "user_id")

    private UserEntity user;
}