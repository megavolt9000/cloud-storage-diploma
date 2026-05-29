package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.FileEntity;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.exception.FileNotFoundException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private FileService fileService;

    private UserEntity user;

    private FileEntity fileEntity;

    @BeforeEach
    void setUp() {

        user = UserEntity.builder()
                .id(1L)
                .login("user")
                .token("token")
                .build();

        fileEntity = FileEntity.builder()
                .id(1L)
                .filename("test.txt")
                .fileSize(100L)
                .path("storage/test.txt")
                .user(user)
                .build();
    }

    @Test
    void uploadFileSuccess() throws Exception {

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user
        )).thenReturn(Optional.empty());

        when(storageService.save(file))
                .thenReturn("storage/test.txt");

        fileService.uploadFile(
                "token",
                "test.txt",
                file
        );

        verify(fileRepository, times(1))
                .save(any(FileEntity.class));
    }

    @Test
    void getFilesSuccess() {

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        when(fileRepository.findAllByUser(user))
                .thenReturn(List.of(fileEntity));

        List<FileEntity> result =
                fileService.getFiles("token", 10);

        assertEquals(1, result.size());

        assertEquals(
                "test.txt",
                result.get(0).getFilename()
        );
    }

    @Test
    void deleteFileSuccess() throws Exception {

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user
        )).thenReturn(Optional.of(fileEntity));

        fileService.deleteFile(
                "token",
                "test.txt"
        );

        verify(storageService, times(1))
                .delete("storage/test.txt");

        verify(fileRepository, times(1))
                .delete(fileEntity);
    }

    @Test
    void renameFileSuccess() throws Exception {

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user
        )).thenReturn(Optional.of(fileEntity));

        when(storageService.rename(
                "storage/test.txt",
                "new.txt"
        )).thenReturn("storage/new.txt");

        fileService.renameFile(
                "token",
                "test.txt",
                "new.txt"
        );

        assertEquals(
                "new.txt",
                fileEntity.getFilename()
        );

        verify(fileRepository, times(1))
                .save(fileEntity);
    }

    @Test
    void downloadFileNotFound() {

        when(userRepository.findByToken("token"))
                .thenReturn(Optional.of(user));

        when(fileRepository.findByFilenameAndUser(
                "missing.txt",
                user
        )).thenReturn(Optional.empty());

        assertThrows(
                FileNotFoundException.class,
                () -> fileService.downloadFile(
                        "token",
                        "missing.txt"
                )
        );
    }

    @Test
    void unauthorizedUser() {

        when(userRepository.findByToken("bad-token"))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedException.class,
                () -> fileService.getFiles(
                        "bad-token",
                        10
                )
        );
    }
}
