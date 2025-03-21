package org.example.expert.domain.common.image.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.image.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            log.error("[IOException] 파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드 실패했습니다.", e);

        } catch (S3Exception e) {
            log.error("[S3Exception] S3 업로드 실패: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 업로드 중 오류 발생", e);

        } catch (SdkClientException e) {
            log.error("[SdkClientException] S3 연결 실패: {}", e.getMessage());
            throw new RuntimeException("S3 연결 실패", e);
        }
    }
}
