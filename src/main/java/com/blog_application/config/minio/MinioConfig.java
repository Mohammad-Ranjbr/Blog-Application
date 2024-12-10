package com.blog_application.config.minio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.region:us-east-1}")
    private String region;

    // MinIO introduces itself as an object storage server with the S3 protocol.
    // This means that all the commands and APIs that you use for AWS S3
    // (such as putObject, getObject, listBuckets, etc.) work the same way in MinIO.
    // S3Client is a client designed to connect to an S3 server. When you use the
    // AWS SDK for Java and connect to MinIO with S3Client, MinIO actually acts as an S3-compatible service.
    // In AWS, each S3 service is located in a specific region (such as us-east-1, us-west-2, etc.)
    // Not required for MinIO but required for compatibility with AWS SDK
    // 1. Virtual Hosted-Style Access (AWS Default)
    // In AWS S3, Virtual Hosted-Style is used by default to construct URLs for accessing objects. This means that the URL will look like this:
    // http://bucket-name.s3.region.amazonaws.com/object-key
    // bucket-name: The name of your bucket.
    // s3.region.amazonaws.com: This is the S3 endpoint in the specified region (for example, s3.us-east-1.amazonaws.com).
    // object-key: The name of the file or object you want to access.
    // This access type is used by default for many AWS-based services and APIs.
    // 2. Path-Style Access (MinIO)
    // In contrast, MinIO (and many similar services) uses Path-Style Access to construct access URLs. In this case, the URL will be as follows:
    // http://endpoint/bucket-name/object-key
    // endpoint: The address of the MinIO server (for example, localhost:9000 or the server's IP address).
    // bucket-name: The name of your bucket.
    // object-key: The name of the file or object you want to access.
    // In this type of URL, the bucket name is included in the path (before the object-key), not in the domain part as seen in the virtual-hosted style.
    // 3. Why do we need pathStyleAccessEnabled(true)?
    // MinIO uses path-style access, so in order for the AWS SDK to communicate with MinIO, we need to enable this access style.
    // If this option is not enabled, the AWS SDK will try to use the virtual hosted style for requests, which is not suitable for MinIO and the requests may not work properly.

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint)) // Specifies which specific endpoint the S3Client should connect to.
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

}
