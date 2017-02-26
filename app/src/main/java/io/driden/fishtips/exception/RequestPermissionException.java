package io.driden.fishtips.exception;

public class RequestPermissionException extends RuntimeException {

    public RequestPermissionException(){
        super();
    }

    public RequestPermissionException(String s){
        super(s);
    }

    public RequestPermissionException(String message, Throwable cause){
        super(message, cause);
    }

    public RequestPermissionException(Throwable cause){
        super(cause);
    }

}
