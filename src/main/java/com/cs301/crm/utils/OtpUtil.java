package com.cs301.crm.utils;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

@Component
public class OtpUtil {
    private Logger logger = LoggerFactory.getLogger(OtpUtil.class);
    private final SecureRandom secureRandom = new SecureRandom();
    private final LoadingCache<String, Integer> oneTimePasswordCache;

    @Autowired
    public OtpUtil(LoadingCache<String, Integer> oneTimePasswordCache) {
        this.oneTimePasswordCache = oneTimePasswordCache;
    }

    public boolean verifyOtp(String id, int otp) throws ExecutionException {
        return otp != oneTimePasswordCache.get(id);
    }

    public int generateOtp(String id) {
        // Generate otp for current user
        oneTimePasswordCache.invalidate(id);
        final int otp = this.generateOtpValue();
        oneTimePasswordCache.put(id, otp);
        logger.info("Generated {} for {}", otp, id);
        return otp;
    }

    public void invalidateOtp(String id) {
        // Generate otp for current user
        oneTimePasswordCache.invalidate(id);
        logger.info("Invalidated cache entry for {}", id);
    }


    private int generateOtpValue() {
        return secureRandom.nextInt(900000) + 100000;
    }
}
