package com.trevisan.CalculadoraMicroServicesDB.Infra;

import com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistMetricService;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MetricAspect {

    private final OperationPersistMetricService metricService;
    private final Timer.Sample sample = Timer.start();

    @Autowired
    public MetricAspect(OperationPersistMetricService metricService) {
        this.metricService = metricService;
    }

    @Around("execution(* com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistService.saveOperationOnDB(..))")
    public Object trackSave(ProceedingJoinPoint joinPoint) {

        Object method = null;
        var metricSuccess = metricService.getInsertOperationSuccessCounter();
        var metricFailed = metricService.getInsertOperationFailedCounter();
        var metricTimer = metricService.getInsertOperationTimer();

        try {
            method = joinPoint.proceed();
            metricSuccess.increment();
            sample.stop(metricTimer);

        } catch (Throwable throwable) {
            log.error("Error saving operation metric to DB: {}", throwable.getMessage());
            metricFailed.increment();
        }

        return method;
    }

    @Around("execution(* com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistService.getAllOperations(..))")
    public Object trackGetOperations(ProceedingJoinPoint joinPoint) {

        Object method = null;
        var metricSuccess = metricService.getGetOperationsSuccessCounter();
        var metricFailed = metricService.getGetOperationsFailedCounter();
        var metricTimer = metricService.getGetOperationsTimer();

        try {

            method = joinPoint.proceed();
            metricSuccess.increment();
            sample.stop(metricTimer);

        } catch (Throwable e) {
            log.error("Error to get operations from DB: {}", e.getMessage());
            metricFailed.increment();
        }

        return method;
    }

    @Around("execution(* com.trevisan.CalculadoraMicroServicesDB.Services.OperationPersistService.getPreviousOperations(..))")
    public Object trackGetPreviousOperations(ProceedingJoinPoint joinPoint) {

        Object method = null;
        var metricSuccess = metricService.getGetPreviousOperationsSuccessCounter();
        var metricFailed = metricService.getGetPreviousOperationsFailedCounter();
        var metricTimer = metricService.getGetPreviousOperationsTimer();

        try {

            method = joinPoint.proceed();
            metricSuccess.increment();
            sample.stop(metricTimer);

        } catch (Throwable e) {
            log.error("Error to get previous operations from DB: {}", e.getMessage());
            metricFailed.increment();
        }

        return method;
    }
}
