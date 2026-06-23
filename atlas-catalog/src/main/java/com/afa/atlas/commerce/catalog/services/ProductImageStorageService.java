package com.afa.atlas.commerce.catalog.services;

import com.afa.atlas.commerce.catalog.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageStorageService {

    private static final String PRODUCTS_PREFIX = "products/";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String upload(final UUID productId, final MultipartFile file) throws IOException {
        final String objectKey = PRODUCTS_PREFIX + productId + "/" + file.getOriginalFilename();

        final PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return s3Properties.publicUrl() + "/" + objectKey;
    }
}