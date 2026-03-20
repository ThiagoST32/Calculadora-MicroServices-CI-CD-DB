package com.trevisan.CalculadoraMicroServicesDB.Services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;

@Service
@Getter
public class OperationPersistMetricService {

    private Counter insertOperationSuccessCounter;
    private Counter insertOperationFailedCounter;

    private Counter getOperationsSuccessCounter;
    private Counter getOperationsFailedCounter;

    private Counter getPreviousOperationsSuccessCounter;
    private Counter getPreviousOperationsFailedCounter;

    private Timer insertOperationTimer;
    private Timer getOperationsTimer;
    private Timer getPreviousOperationsTimer;

    private final MeterRegistry meterRegistry;

    @Autowired
    public OperationPersistMetricService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {

        insertOperationSuccessCounter = Counter.builder("com.application.operationService.insertOperations")
                .tag("status", "SUCCESS")
                .register(meterRegistry);

        insertOperationFailedCounter = Counter.builder("com.application.operationService.insertOperations")
                .tag("status", "FAILED")
                .register(meterRegistry);

        getOperationsSuccessCounter = Counter.builder("com.application.operationService.getAllOperations")
                .tag("status", "SUCCESS")
                .register(meterRegistry);

        getOperationsFailedCounter = Counter.builder("com.application.operationService.getAllOperations")
                .tag("status", "FAILED")
                .register(meterRegistry);

        getPreviousOperationsSuccessCounter = Counter.builder("com.application.operationService.getPreviousOperations")
                .tag("status", "SUCCESS")
                .register(meterRegistry);

        getPreviousOperationsFailedCounter = Counter.builder("com.application.operationService.getPreviousOperations")
                .tag("status", "FAILED")
                .register(meterRegistry);

        insertOperationTimer = Timer.builder("com.application.operationService.insert.time")
                .description("Time of insert operation on DB")
                .register(meterRegistry);

        getOperationsTimer = Timer.builder("com.application.operationService.getOperations.time")
                .description("Time of execution get all operations")
                .register(meterRegistry);

        getPreviousOperationsTimer = Timer.builder("com.application.operationService.getPreviousOperations.time")
                .description("Time of execution get previous operations")
                .register(meterRegistry);
    }
}
