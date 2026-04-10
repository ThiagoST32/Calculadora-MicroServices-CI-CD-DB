package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions;

public class InvalidDatabaseNameException extends RuntimeException {
    public InvalidDatabaseNameException(String dbType) {
        super("The SQL file to execute to this database could not be found {}\n Please verify that your database is compatible with application" + dbType);
    }
}
