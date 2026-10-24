package au.edu.rmit.ozagrimate.irrigation.exception;

/** Maps to HTTP 409 - request conflicts with the current state. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
