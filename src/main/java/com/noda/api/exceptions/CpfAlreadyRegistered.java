package com.noda.api.exceptions;

public class CpfAlreadyRegistered extends RuntimeException {
    public CpfAlreadyRegistered(String message) {
        super(message);
    }
}
