package com.doganmehmet.app.aspect;

import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class LoggingAspect {
    private static final String FILE_NAME = "logs/ActivityLog.log";

    @Before("execution(* com.doganmehmet.app.controller..*(..))")
    public void logBeforeController(JoinPoint joinPoint)
    {
        var httpMethod = getHttpMethod(joinPoint);
        writeToFile("[CONTROLLER REQUEST] HTTP: " + httpMethod + " | Method: " + methodSignature(joinPoint));
    }

    @AfterReturning("execution(* com.doganmehmet.app.controller..*(..))")
    public void logAfterController(JoinPoint joinPoint)
    {
        writeToFile("[CONTROLLER SUCCESS] Method: " + methodSignature(joinPoint));
    }

    @Before("execution(* com.doganmehmet.app.service..*(..))")
    public void logBeforeService(JoinPoint joinPoint)
    {
        writeToFile("[SERVICE REQUEST] Method: " + methodSignature(joinPoint));
    }

    @AfterReturning("execution(* com.doganmehmet.app.service..*(..))")
    public void logAfterService(JoinPoint joinPoint)
    {
        writeToFile("[SERVICE SUCCESS] Method: " + methodSignature(joinPoint));
    }

    @AfterThrowing( pointcut = "execution(* com.doganmehmet.app..*(..))", throwing = "exception")
    public void logExceptions(JoinPoint joinPoint, Exception exception)
    {
        writeToFile("[ERROR] Method: " + methodSignature(joinPoint) + " | Exception: " + exception.getMessage());
    }

    private void writeToFile(String message)
    {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        new java.io.File("logs").mkdirs();
        try (var writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(LocalDateTime.now().format(formatter) + " - " + message);
            writer.newLine();
        } catch (IOException e) {
            throw new ApiException(MyError.WRITE_TO_FILE_EXCEPTION);
        }
    }

    private String methodSignature(JoinPoint joinPoint)
    {
        var className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        var methodName = joinPoint.getSignature().getName();
        return className + "." + methodName + "()";
    }

    private String getHttpMethod(JoinPoint joinPoint)
    {
        var methodSignature = (MethodSignature) joinPoint.getSignature();

        if (methodSignature.getMethod().isAnnotationPresent(GetMapping.class))
            return "GET";
        if (methodSignature.getMethod().isAnnotationPresent(PostMapping.class))
            return "POST";
        if (methodSignature.getMethod().isAnnotationPresent(DeleteMapping.class))
            return "DELETE";
        if (methodSignature.getMethod().isAnnotationPresent(PutMapping.class))
            return "PUT";
        if (methodSignature.getMethod().isAnnotationPresent(PatchMapping.class))
            return "PATCH";

        return "UNKNOWN";
    }
}
