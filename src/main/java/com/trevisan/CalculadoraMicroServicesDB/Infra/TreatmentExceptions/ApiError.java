package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions;

import lombok.Builder;
import org.springframework.http.HttpStatusCode;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record ApiError (
        String messageError,

        int errorCode,

        String statusCode,

        OffsetDateTime timestamp,

        Throwable cause,

        List<String> messagesErrors){}
