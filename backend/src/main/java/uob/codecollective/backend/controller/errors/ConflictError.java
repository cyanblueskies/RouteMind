package uob.codecollective.backend.controller.errors;

public class ConflictError extends RuntimeException {
    public ConflictError(String msg) {
        super(msg);
    }
}
