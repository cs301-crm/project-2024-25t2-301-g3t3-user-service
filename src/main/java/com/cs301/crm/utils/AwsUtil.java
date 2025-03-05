package com.cs301.crm.utils;

import com.cs301.crm.exceptions.AwsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * @author: gav
 * @version: 1.0
 * @since: 24-09-06
 * @description: Utility class for AWS services
 */
@Component
public class AwsUtil {
    private Logger logger = LoggerFactory.getLogger(AwsUtil.class);
    public String getValueFromSecretsManager(
            String secretName
    ) {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        GetSecretValueRequest secretValueRequest =
                GetSecretValueRequest.builder().secretId(secretName).build();

        GetSecretValueResponse secretValueResponse = null;

        try {
            secretValueResponse = client.getSecretValue(secretValueRequest);
        } catch (SdkException e) {
            logger.error(e.getMessage());
            throw new AwsException("Failed to get secret value");
        }

        if (secretValueResponse == null) {
            return null;
        }

        return secretValueResponse.secretString();
    }
}