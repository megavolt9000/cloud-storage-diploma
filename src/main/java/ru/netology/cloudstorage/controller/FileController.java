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
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud")

public class FileController {

    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(

            @RequestHeader("auth-token")
            String authToken,

            @RequestParam("filename")
            String filename,

            @RequestPart("file")
            MultipartFile file

    ) throws Exception {

        fileService.uploadFile(
                authToken,
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

        List<FileEntity> files =
                fileService.getFiles(
                        authToken,
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

        fileService.deleteFile(
                authToken,
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

        System.out.println("OLD = " + filename);
        System.out.println("NEW = " + request.getFilename());

        fileService.renameFile(

                authToken,
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

        byte[] file = fileService.downloadFile(
                authToken,
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
