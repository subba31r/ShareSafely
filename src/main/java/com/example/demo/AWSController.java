package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/s3")
public class AWSController {

    
    private final S3Client s3Client;
    private final SecretManagerService secretManagerService;

    public AWSController(S3Client s3Client, SecretManagerService secretManagerService) {
        this.s3Client = s3Client;
        this.secretManagerService = secretManagerService;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("expirationSeconds") int expirationSeconds) throws Exception {
        try {
            // Get the credentials from Secrets Manager
            JsonNode secret = secretManagerService.getSecret();
            //accessing bucket name from secrets
            String bucketName = secret.get("s3_bucket_name").asText();
             
            long expirationTimestamp = Instant.now().getEpochSecond() + expirationSeconds;
            // Convert MultipartFile to File for S3 upload
            File tempFile = convertMultipartToFile(file);
            String fileName = expirationTimestamp +"_"+ UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Create a PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            // Upload file to S3
            s3Client.putObject(putObjectRequest, tempFile.toPath());

            String fileUrl = generatePresignedUrl(bucketName, fileName, expirationSeconds);
            return ResponseEntity.ok("File uploaded successfully! Access your file at: " + fileUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }

    }

    // Convert MultipartFile to File for S3 upload
    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload_", file.getOriginalFilename());
        tempFile.deleteOnExit();
        file.transferTo(tempFile);
        return tempFile;
    }

    private String generatePresignedUrl(String bucketName, String fileName, int expirationSeconds) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationSeconds))
                    .getObjectRequest(b -> b.bucket(bucketName).key(fileName))
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

}