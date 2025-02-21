package com.example.demo;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretManagerService {

    @Autowired
    private SecretsManagerClient secretsManagerClient;

    public JsonNode getSecret() throws Exception {
        // AWS Secrets manager Secret Name
        String secretName = "my-aws-credentials";  
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse secretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

        String secret = secretValueResponse.secretString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(secret);
    }
}
