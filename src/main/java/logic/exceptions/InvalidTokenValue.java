package logic.exceptions;

import java.io.Serial;

public class InvalidTokenValue extends Exception{
    @Serial
    private static final long serialVersionUID = 218873282341289L;

    public InvalidTokenValue(String message) {
        super(message);
    }

    public InvalidTokenValue(String message, Throwable cause){
        super(message + cause.getMessage());
    }

}
