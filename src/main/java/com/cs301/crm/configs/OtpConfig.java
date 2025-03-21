package com.cs301.crm.configs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OtpConfig {

    @Value("${otp.expiration}")
    private long expirationSeconds;

    @Bean
    public LoadingCache<String, Integer> otpCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(expirationSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @NotNull
                    public Integer load(@NotNull String key) {
                        return 0;
                    }
                });
    }
}
