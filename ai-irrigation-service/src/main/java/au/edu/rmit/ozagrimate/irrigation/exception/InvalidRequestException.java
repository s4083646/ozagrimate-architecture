package au.edu.rmit.ozagrimate.irrigation.exception;

/** Maps to HTTP 400 - request body fails validation. */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
