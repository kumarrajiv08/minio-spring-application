package com.minio.demo.services;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author rajiv.kumar
 */
@Service
public class MinioService {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.expire-time-in-seconds}")
    private int expireTimeInSeconds;


    private MinioClient createMinioClient() {

        return MinioClient.builder()
                          .endpoint(minioUrl)
                          .credentials(accessKey, secretKey)
                          .build();
    }

    public boolean checkObjectExists(MinioClient minioClient,
                                     String objectName) {

        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                                  .bucket(bucketName)
                                  .object(objectName)
                                  .build()) != null;
        }
        catch (Exception e) {

            return false;
        }

    }

    public String getPresignedUrlForUpload(String objectName) throws Exception {

        try {
            MinioClient minioClient = createMinioClient();
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                System.out.println("not exists");
            }

            boolean objectExists = checkObjectExists(minioClient, objectName);
            if (!objectExists) {
                minioClient.putObject(PutObjectArgs.builder()
                                                   .bucket(bucketName)
                                                   .object(objectName)
                                                   .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                                   .build());
            }

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                                             .bucket(bucketName)
                                             .object(objectName)
                                             .method(Method.PUT)
                                             .expiry(expireTimeInSeconds, TimeUnit.SECONDS)
                                             .build());

            return "curl -X PUT -T " + "/Users/rajiv.kumar/Desktop/miniodata/" + objectName + " \"" + url + "\"";


        }
        catch (MinioException e) {
            throw new Exception("Error generating presigned URL for upload", e);
        }

    }

    public String getPresignedUrlForDownload(String objectName) throws Exception {

        try {
            MinioClient minioClient = createMinioClient();
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                return "Bucket does not exists";
            }
            ;
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                                             .bucket(bucketName)
                                             .object(objectName)
                                             .method(Method.GET)
                                             .expiry(expireTimeInSeconds, TimeUnit.SECONDS)
                                             .build());
        }
        catch (MinioException e) {
            throw new Exception("Error generating presigned URL for download", e);
        }
    }
}
