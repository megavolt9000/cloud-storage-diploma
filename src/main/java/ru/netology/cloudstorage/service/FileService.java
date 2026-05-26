package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileResponse;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exception.FileNotFoundException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class FileService {

    private final UserRepository userRepository;

    private final FileRepository fileRepository;

    private final StorageService storageService;

    public void uploadFile(

            String authToken,
            String filename,
            MultipartFile file

    ) throws Exception {

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

    public List<FileResponse> getFiles(

            String authToken,
            Integer limit

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

        List<FileEntity> files =
                fileRepository.findAllByUser(user);

        return files.stream()

                .limit(limit == null ? files.size() : limit)

                .map(file ->

                        FileResponse.builder()

                                .filename(file.getFilename())

                                .size(file.getFileSize())

                                .build()
                )

                .toList();
    }

    public void deleteFile(

            String authToken,
            String filename

    ) throws Exception {
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

        FileEntity file = fileRepository

                .findByFilenameAndUser(
                        filename,
                        user
                )

                .orElseThrow(() ->
                        new RuntimeException(
                                "File not found"
                        )
                );

        storageService.delete(
                file.getPath()
        );

        fileRepository.delete(file);
    }

    public void renameFile(

            String authToken,
            String oldFilename,
            String newFilename

    ) throws Exception {
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

        FileEntity file = fileRepository

                .findByFilenameAndUser(
                        oldFilename,
                        user
                )

                .orElseThrow(() ->
                        new RuntimeException(
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

            String authToken,
            String filename

    ) throws Exception {
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