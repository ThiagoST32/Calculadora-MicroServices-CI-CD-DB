package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions;

import java.sql.SQLException;

public class FailedToGetConnectionException extends SQLException {
    public FailedToGetConnectionException() {
        super("Connection to the database could not be found");
    }
}
