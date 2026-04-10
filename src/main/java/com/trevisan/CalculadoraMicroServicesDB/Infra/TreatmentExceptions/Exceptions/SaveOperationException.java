package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions;

public class SaveOperationException extends RuntimeException {
    public SaveOperationException() {
        super("Error saving operation");
    }
}
