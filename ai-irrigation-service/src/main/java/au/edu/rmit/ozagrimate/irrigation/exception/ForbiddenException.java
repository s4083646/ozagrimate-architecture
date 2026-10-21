package au.edu.rmit.ozagrimate.irrigation.exception;

/** Maps to HTTP 403 - caller's role is not allowed to perform the action. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
