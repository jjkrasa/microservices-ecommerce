package com.ecommerce.productservice.service;

import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private MultipartFile multipartFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString());
    }

    @Test
    void saveImage_shouldSaveAndReturnUrl() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        doAnswer(invocation -> {
            Path dest = (Path) invocation.getArgument(0, java.io.File.class).toPath();
            Files.createFile(dest);
            return null;
        }).when(multipartFile).transferTo(any(java.io.File.class));

        String url = imageService.saveImage(multipartFile);

        assertTrue(url.startsWith("/images/"));
        assertTrue(Files.exists(tempDir.resolve(url.replace("/images/", ""))));
    }

    @Test
    void saveImage_shouldThrowBadRequest_whenEmptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> imageService.saveImage(multipartFile));
    }

    @Test
    void saveImage_shouldThrowBadRequest_whenInvalidType() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        assertThrows(BadRequestException.class, () -> imageService.saveImage(multipartFile));
    }

    @Test
    void saveImage_shouldThrowInternalServerException_whenIOException() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        doThrow(new IOException()).when(multipartFile).transferTo(any(java.io.File.class));

        assertThrows(InternalServerException.class, () -> imageService.saveImage(multipartFile));
    }

    @Test
    void replaceProductImage_shouldDeleteOldAndReturnNewUrl() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("new.jpg");
        doAnswer(invocation -> {
            Path dest = (Path) invocation.getArgument(0, java.io.File.class).toPath();
            Files.createFile(dest);
            return null;
        }).when(multipartFile).transferTo(any(java.io.File.class));

        String oldFileName = "old.jpg";
        Path oldFile = tempDir.resolve(oldFileName);
        Files.createFile(oldFile);
        String oldImageUrl = "/images/" + oldFileName;

        String url = imageService.replaceProductImage(oldImageUrl, multipartFile);

        assertTrue(url.startsWith("/images/"));
        assertFalse(Files.exists(oldFile));
    }

    @Test
    void deleteImage_shouldDeleteFile() throws IOException {
        String fileName = "delete.jpg";
        Path file = tempDir.resolve(fileName);
        Files.createFile(file);
        String imageUrl = "/images/" + fileName;

        assertTrue(Files.exists(file));
        imageService.deleteImage(imageUrl);

        assertFalse(Files.exists(file));
    }

    @Test
    void deleteImage_shouldNotThrow_whenImageUrlIsNullOrBlank() {
        assertDoesNotThrow(() -> imageService.deleteImage(null));
        assertDoesNotThrow(() -> imageService.deleteImage(" "));
    }

    @Test
    void deleteImage_shouldThrowInternalServerException() throws IOException {
        String fileName = "fail.jpg";
        Path file = tempDir.resolve(fileName);
        Files.createFile(file);
        String imageUrl = "/images/" + fileName;
        // Make file read-only to cause IOException
        file.toFile().setReadOnly();

        assertThrows(InternalServerException.class, () -> imageService.deleteImage(imageUrl));
    }
}
