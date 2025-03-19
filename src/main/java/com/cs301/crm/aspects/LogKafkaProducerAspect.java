package com.cs301.crm.aspects;

import com.cs301.crm.exceptions.AspectExecutionException;
import com.cs301.crm.producers.LogKafkaProducer;
import com.cs301.crm.protobuf.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
public class KafkaLogProducerAspect {

    private static final Logger logger = LoggerFactory.getLogger(KafkaLogProducerAspect.class);

    private final LogKafkaProducer logKafkaProducer;

    @Autowired
    public KafkaLoggingAspect(LogKafkaProducer logKafkaProducer) {
        this.logKafkaProducer = logKafkaProducer;
    }

    @Around("execution(* com.cs301.crm.controllers..*(..))")
    public Object produce(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            logger.info("Initial method executed successfully.")
            // Get the method being called
            Method method = getTargetMethod(joinPoint);

            // Get the specific annotation used (PostMapping, GetMapping, etc.)
            String annotationType = getMappingAnnotation(method);

            String methodName = joinPoint.getSignature().getName();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || authentication.isAuthenticated()) {
                return result;
            }

            if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
                return result;
            }

            String username = userDetails.getUsername();

            Log logMessage = Log.newBuilder()
                    .setLogId(UUID.randomUUID().toString())
                    .setTransactionType(annotationType)
                    .setAction(methodName)
                    .setActor(username)
                    .setTimestamp(Instant.now().toString())
                    .build();

            logger.info("Pushing message to Kafka");
            logKafkaProducer.produceMessage(logMessage);
            return result;
        } catch (Exception e) {
            throw new AspectExecutionException(e.getMessage());
        }
    }

    // Utility to get the actual method being called
    private Method getTargetMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
        return targetClass.getMethod(methodName, parameterTypes);
    }

    // Utility to check which annotation is present
    private String getMappingAnnotation(Method method) {
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        if (method.isAnnotationPresent(RequestMapping.class)) return "REQUEST_MAPPING";
        return "UNKNOWN";
    }
}
