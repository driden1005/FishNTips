package io.driden.fishtips.exception;

public class NoPlayServiceException extends RuntimeException {

    public NoPlayServiceException(){
        super();
    }

    public NoPlayServiceException(String s){
        super(s);
    }

    public NoPlayServiceException(String message, Throwable cause){
        super(message, cause);
    }

    public NoPlayServiceException(Throwable cause){
        super(cause);
    }

}
