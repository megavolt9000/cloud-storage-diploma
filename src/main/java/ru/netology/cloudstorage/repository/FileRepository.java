package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface FileRepository
        extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByUser(UserEntity user);

    Optional<FileEntity> findByFilenameAndUser(
            String filename,
            UserEntity user
    );

    void deleteByFilenameAndUser(
            String filename,
            UserEntity user
    );
}