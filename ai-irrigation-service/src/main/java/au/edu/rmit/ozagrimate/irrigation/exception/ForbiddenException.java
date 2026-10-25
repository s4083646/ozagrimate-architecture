package au.edu.rmit.ozagrimate.irrigation.exception;

// 403
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
