package ru.netology.cloudstorage.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardCopyOption;

import java.util.UUID;

@Service

public class StorageService {

    @Value("${storage.location}")

    private String storageLocation;

    public String save(
            MultipartFile file
    ) throws IOException {

        Path storagePath =
                Paths.get(storageLocation);

        if (!Files.exists(storagePath)) {

            Files.createDirectories(storagePath);
        }

        String uniqueFilename =
                UUID.randomUUID()
                        + "_"
                        + file.getOriginalFilename();

        Path destination =
                storagePath.resolve(uniqueFilename);

        Files.copy(

                file.getInputStream(),

                destination,

                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.toString();
    }
    public void delete(
            String path
    ) throws IOException {

        Path filePath =
                Paths.get(path);

        Files.deleteIfExists(filePath);
    }
    public String rename(

            String oldPath,
            String newFilename

    ) throws IOException {

        Path oldFile =
                Paths.get(oldPath);

        String uniqueFilename =
                UUID.randomUUID()
                        + "_"
                        + newFilename;

        Path newFile = oldFile
                .getParent()
                .resolve(uniqueFilename);

        Files.move(
                oldFile,
                newFile,
                StandardCopyOption.REPLACE_EXISTING
        );

        return newFile.toString();
    }
    public byte[] downloadFile(
            String path
    ) throws Exception {

        return Files.readAllBytes(
                Path.of(path)
        );
    }
}