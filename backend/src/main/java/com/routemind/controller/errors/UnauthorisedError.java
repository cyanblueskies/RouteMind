package com.routemind.controller.errors;

public class UnauthorisedError extends RuntimeException {
    public UnauthorisedError(String msg) {
        super(msg);
    }
}
