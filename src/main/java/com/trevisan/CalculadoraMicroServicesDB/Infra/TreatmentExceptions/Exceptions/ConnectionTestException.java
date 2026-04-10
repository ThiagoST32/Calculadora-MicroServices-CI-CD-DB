package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions;

public class ConnectionTestException extends RuntimeException {
    public ConnectionTestException() {
        super("""
                Unable to verify the connection to the database!
                 \
                We were unable to test the connection with a database call;
                 Please check your credentials or verify that the database is running.""");
    }
    public ConnectionTestException(Throwable cause) {
        super("""
                Unable to verify the connection to the database!
                 \
                We were unable to test the connection with a database call;
                 Please check your credentials or verify that the database is running.""", cause);
    }
}
