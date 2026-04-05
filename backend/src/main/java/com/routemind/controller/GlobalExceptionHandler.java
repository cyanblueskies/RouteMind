package com.routemind.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.routemind.controller.errors.*;
import com.routemind.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 404 (not found)
    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ErrorResponse> handleUnauthorised(NotFoundError ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }


    // 400 (bad request)
    @ExceptionHandler(BadRequestError.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestError ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Bad Request: " + ex.getMessage()));
    }

    // 401 (unauthorised)
    @ExceptionHandler(UnauthorisedError.class)
    public ResponseEntity<ErrorResponse> handleUnauthorised(UnauthorisedError ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 403 (forbidden)
    @ExceptionHandler(ForbiddenError.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenError ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // 409 (conflict)
    @ExceptionHandler(ConflictError.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictError ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // wildcard for misc errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleWildcard(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }
}
