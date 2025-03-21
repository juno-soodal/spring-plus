package org.example.expert.domain.common.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void uploadFile(MultipartFile uploadImage, String key);
}
