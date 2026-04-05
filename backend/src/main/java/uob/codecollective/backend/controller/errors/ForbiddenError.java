package uob.codecollective.backend.controller.errors;

public class ForbiddenError extends RuntimeException {
    public ForbiddenError(String msg) {
        super(msg);
    }
}
