package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exception.FileNotFoundException;
import ru.netology.cloudstorage.repository.FileRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class FileService {

    private final FileRepository fileRepository;

    private final StorageService storageService;

    public void uploadFile(

            UserEntity user,
            String filename,
            MultipartFile file

    ) throws Exception {

        boolean exists = fileRepository
                .findByFilenameAndUser(filename, user)
                .isPresent();

        if (exists) {

            throw new RuntimeException(
                    "File already exists"
            );
        }

        String savedPath =
                storageService.save(file);

        FileEntity fileEntity =
                FileEntity.builder()

                        .filename(filename)

                        .fileSize(file.getSize())

                        .path(savedPath)

                        .uploadDate(LocalDateTime.now())

                        .user(user)

                        .build();

        fileRepository.save(fileEntity);
    }

    public List<FileEntity> getFiles(

            UserEntity user,
            Integer limit

    ) {

        List<FileEntity> files =
                fileRepository.findAllByUser(user);

        return files.stream()

                .limit(limit == null ? files.size() : limit)

                .toList();
    }

    public void deleteFile(

            UserEntity user,
            String filename

    ) throws Exception {

        FileEntity file = fileRepository

                .findByFilenameAndUser(
                        filename,
                        user
                )

                .orElseThrow(() ->
                        new FileNotFoundException(
                                "File not found"
                        )
                );

        storageService.delete(
                file.getPath()
        );

        fileRepository.delete(file);
    }

    public void renameFile(

            UserEntity user,
            String oldFilename,
            String newFilename

    ) throws Exception {

        FileEntity file = fileRepository

                .findByFilenameAndUser(
                        oldFilename,
                        user
                )

                .orElseThrow(() ->
                        new FileNotFoundException(
                                "File not found"
                        )
                );

        String newPath =
                storageService.rename(
                        file.getPath(),
                        newFilename
                );

        file.setFilename(newFilename);

        file.setPath(newPath);

        fileRepository.save(file);
    }

    public byte[] downloadFile(

            UserEntity user,
            String filename

    ) throws Exception {

        FileEntity fileEntity = fileRepository

                .findByFilenameAndUser(
                        filename,
                        user
                )

                .orElseThrow(() ->
                        new FileNotFoundException(
                                "File not found"
                        )
                );

        return storageService.downloadFile(
                fileEntity.getPath()
        );
    }
}
