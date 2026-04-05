package uob.codecollective.backend.controller.errors;

public class UnauthorisedError extends RuntimeException {
    public UnauthorisedError(String msg) {
        super(msg);
    }
}
