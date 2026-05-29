package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileResponse;
import ru.netology.cloudstorage.dto.RenameFileRequest;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud")

public class FileController {

    private final FileService fileService;

    private final AuthService authService;

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam("filename")
            String filename,

            @RequestPart("file")
            MultipartFile file

    ) throws Exception {

        UserEntity user =
                authService.getUserByToken(authToken);

        fileService.uploadFile(
                user,
                filename,
                file
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getFiles(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam(required = false)
            Integer limit

    ) {

        UserEntity user =
                authService.getUserByToken(authToken);

        List<FileEntity> files =
                fileService.getFiles(
                        user,
                        limit
                );

        List<FileResponse> response = files.stream()

                .map(file ->

                        FileResponse.builder()

                                .filename(file.getFilename())

                                .size(file.getFileSize())

                                .build()
                )

                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam("filename")
            String filename

    ) throws Exception {

        UserEntity user =
                authService.getUserByToken(authToken);

        fileService.deleteFile(
                user,
                filename
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping(
            value = "/file",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> renameFile(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam("filename")
            String filename,

            @RequestBody
            RenameFileRequest request

    ) throws Exception {

        UserEntity user =
                authService.getUserByToken(authToken);

        fileService.renameFile(

                user,
                filename,
                request.getFilename()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam("filename")
            String filename

    ) throws Exception {

        UserEntity user =
                authService.getUserByToken(authToken);

        byte[] file = fileService.downloadFile(
                user,
                filename
        );

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )

                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )

                .body(file);
    }
}
