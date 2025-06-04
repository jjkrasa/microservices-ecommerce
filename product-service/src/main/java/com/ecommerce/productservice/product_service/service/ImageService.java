package com.ecommerce.productservice.product_service.service;

import com.ecommerce.exceptionlib.ErrorCode;
import com.ecommerce.exceptionlib.exception.BadRequestException;
import com.ecommerce.exceptionlib.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${product.image.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");

    public String saveImage(MultipartFile file) {

        if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType().toLowerCase())) {
            throw new BadRequestException(ErrorCode.INVALID_IMAGE_FORMAT.getMessage());
        }

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = Paths.get(uploadDir).toAbsolutePath().resolve(fileName).normalize();
            Files.createDirectories(destination.getParent());

            file.transferTo(destination.toFile());

            return "/images/" + fileName;
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.IMAGE_UPLOAD_FAILED.getMessage());
        }
    }

    public String replaceProductImage(String oldImageUrl, MultipartFile file) {
        String imageUrl = saveImage(file);
        deleteImage(oldImageUrl);

        return imageUrl;
    }
    
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        
        try {
            String fileName = Paths.get(imageUrl).getFileName().toString();
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.DELETE_IMAGE_FAILED.getMessage());
        }
    }
}
