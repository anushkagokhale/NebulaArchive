package org.example;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

public class MinioManager {
    // These match the API links in your PowerShell
    private final String endpoint = "http://127.0.0.1:9000";
    private final String accessKey = "minioadmin";
    private final String secretKey = "minioadmin";

    private final MinioClient minioClient;

    public MinioManager() {
        // Initialize the real MinIO Client
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void uploadFile(String bucketName, String filePath, String fileName) {
        try {
            // MinIO buckets must be lowercase
            String bName = bucketName.toLowerCase();

            // 1. Create the bucket (folder) if it doesn't exist
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bName).build());
                System.out.println("Cloud Bucket Created: " + bName);
            }

            // 2. Upload the actual file to the server
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bName)
                            .object(fileName)
                            .filename(filePath)
                            .build());

            System.out.println("REAL CLOUD SUCCESS: " + fileName + " is live in MinIO!");

        } catch (Exception e) {
            System.err.println("MinIO Server Error: " + e.getMessage());
        }
    }
}