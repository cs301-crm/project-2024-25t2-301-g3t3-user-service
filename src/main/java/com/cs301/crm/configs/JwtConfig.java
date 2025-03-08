package com.cs301.crm.configs;

import com.cs301.crm.exceptions.AwsException;
import com.cs301.crm.exceptions.JwtCreationException;
import com.cs301.crm.utils.AwsUtil;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
    private Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Value("${jwt.id}")
    private String keyId;

    @Bean
    public RSAPublicKey rsaPublicKey(
            @Value("${jwt.public.key}") String publicKeyString
    ) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(
                new X509EncodedKeySpec(publicKeyBytes)
        );
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(AwsUtil awsUtil) {
        try {
            String privateKeyStr = awsUtil.getValueFromSecretsManager(
                    "JWTPrivateKey"
            );

            if (privateKeyStr == null) {
                throw new AwsException("JWT Private Key not found");
            }

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    privateKeyBytes
            );

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            return (RSAPrivateKey) privateKey;
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage());
            throw new JwtCreationException("Invalid private key specification", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            throw new JwtCreationException("RSA algorithm not available", e);
        }
    }

    @Bean
    public RSAKey rsaKey(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return ((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
