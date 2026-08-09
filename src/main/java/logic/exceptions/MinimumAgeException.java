package logic.exceptions;

import java.io.Serial;

public class MinimumAgeException extends Exception{
    @Serial
    private static final long serialVersionUID = 2289L;

    public MinimumAgeException(String message) {
        super(message);
    }

    public MinimumAgeException(String message, Throwable cause){
        super(message + cause.getMessage());
    }
}
