package ca.cmpt276.votosurveytaker.exception;

public class GooglePlayServiceUnavailableException extends Exception{

    public GooglePlayServiceUnavailableException() {}

    public GooglePlayServiceUnavailableException(String message)
    {
       super(message);
    }
}
