package au.edu.rmit.ozagrimate.irrigation.exception;

/** Maps to HTTP 404 - farm or recommendation does not exist. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
