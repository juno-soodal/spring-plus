package org.example.expert.domain.common.image.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.image.dto.ImageResponse;
import org.example.expert.domain.common.image.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageResponse> uploadFile(@RequestPart MultipartFile uploadImage, @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(imageService.uploadImage(uploadImage, authUser.getId()));
    }
}
