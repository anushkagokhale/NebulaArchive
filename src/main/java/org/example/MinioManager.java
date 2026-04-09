package org.example;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

public class MinioManager {
    private final MinioClient minioClient = MinioClient.builder()
            .endpoint("http://127.0.0.1:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    // Updated to accept two arguments to match your FileReader
    public void uploadFile(String fileName, String filePath) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("nebula-vault")
                            .object(fileName)
                            .filename(filePath)
                            .build());
        } catch (Exception e) {
            System.err.println("MinIO Error: " + e.getMessage());
        }
    }
}