package com.routemind.controller.errors;

public class BadRequestError extends RuntimeException {
    public BadRequestError(String msg) {
        super(msg);
    }
}
