package logic.exceptions;

import java.io.Serial;

public class TextTooLongException extends Exception{
    @Serial
    private static final long serialVersionUID = 1875L;
    private final String cause;

    public TextTooLongException(String message){
        this.cause = message;
    }

    @Override
    public String getMessage(){
        return this.cause;
    }
}
