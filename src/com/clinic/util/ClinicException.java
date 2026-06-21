package com.clinic.util;

public class ClinicException extends Exception {

    public enum Type { DOUBLE_BOOKING, INVALID_DATE, INVALID_INPUT, DATABASE_ERROR, PAYMENT_ERROR }

    private final Type type;

    public ClinicException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() { return type; }
}
