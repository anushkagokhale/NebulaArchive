package org.example;

import io.minio.*;

public class MinioManager {
    private final MinioClient minioClient = MinioClient.builder()
            .endpoint("http://127.0.0.1:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    public void uploadToCategory(String fileName, String filePath, String category) {
        try {
            String bucketName = category.toLowerCase().replaceAll("[^a-z0-0]", "-");
            if (bucketName.isEmpty()) bucketName = "uncategorized";

            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .filename(filePath)
                            .build());
        } catch (Exception e) {
            System.err.println("MinIO Error: " + e.getMessage());
        }
    }
}