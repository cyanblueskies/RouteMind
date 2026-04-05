package uob.codecollective.backend.controller.errors;

public class BadRequestError extends RuntimeException {
    public BadRequestError(String msg) {
        super(msg);
    }
}
