package org.example.expert.domain.common.image.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.image.dto.ImageResponse;
import org.example.expert.domain.common.image.entity.Image;
import org.example.expert.domain.common.image.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileStorageService fileStorageService;
    private final ImageRepository imageRepository;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            IMAGE_JPEG_VALUE,
            IMAGE_PNG_VALUE
    );

    public ImageResponse uploadImage(MultipartFile uploadImage, Long userId) {

        validateImageType(uploadImage);

        String originalFilename = uploadImage.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;
        String key = "images/" + storedFilename;
        fileStorageService.uploadFile(uploadImage, key);

        Image image = Image.builder()
                .storedFilename(storedFilename)
                .originalFilename(originalFilename)
                .fileSize(uploadImage.getSize())
                .fileUrl(key)
                .fileType(uploadImage.getContentType())
                .build();
        imageRepository.save(image);

        return ImageResponse.from(image);

    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            return ""; // 확장자가 없는 경우
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private void validateImageType(MultipartFile uploadImage) {
        if (!ALLOWED_IMAGE_TYPES.contains(uploadImage.getContentType())) {
            throw new InvalidRequestException("지원하지 않는 이미지 형식입니다.");
        }

    }
}
