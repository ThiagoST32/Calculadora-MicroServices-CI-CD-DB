package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions;

public class FailedToExecuteOperationsException extends RuntimeException {
    public FailedToExecuteOperationsException(Throwable cause) {
        super("Cannot perform this operation\nVerify if uer does not have permission to perform this operation", cause);
    }
}
