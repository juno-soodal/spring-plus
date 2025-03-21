package org.example.expert.domain.common.image.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.image.entity.Image;

@Getter

public class ImageResponse {



    private final long id;
    private final String imageUrl;
    private final String imageType;

    private ImageResponse(long id, String imageUrl, String imageType) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }


    public static ImageResponse from(Image image) {
        return new ImageResponse(image.getId(), image.getFileUrl(), image.getFileType());
    }
}
