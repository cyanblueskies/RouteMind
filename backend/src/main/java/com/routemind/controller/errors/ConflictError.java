package com.routemind.controller.errors;

public class ConflictError extends RuntimeException {
    public ConflictError(String msg) {
        super(msg);
    }
}
