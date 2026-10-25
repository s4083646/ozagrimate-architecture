package au.edu.rmit.ozagrimate.irrigation.exception;

// 400
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
