package io.driden.fishtips.exception;

public class NoMethodFoundException extends RuntimeException {

    public NoMethodFoundException(){
        super();
    }

    public NoMethodFoundException(String s){
        super(s);
    }

    public NoMethodFoundException(String message, Throwable cause){
        super(message, cause);
    }

    public NoMethodFoundException(Throwable cause){
        super(cause);
    }

}
