package logic.exceptions;

import java.io.Serial;

public class InvalidValueException extends Exception{
    @Serial
    private static final long serialVersionUID = 2188732812891289L;

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause){
        super(message + cause.getMessage());
    }
}
