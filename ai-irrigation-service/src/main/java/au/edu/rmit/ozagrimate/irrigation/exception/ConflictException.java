package au.edu.rmit.ozagrimate.irrigation.exception;

// 409
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
