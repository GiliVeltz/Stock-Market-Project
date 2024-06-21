package ServiceLayer;

import com.fasterxml.jackson.annotation.JsonInclude;

// Purpose: Response class for handling the response of the service layer.
@JsonInclude
public class Response<T> {
    private String _errorMessage;
    private T _returnValue;

    // consructor.
    public Response() {
    }

    public Response(String errorMessage, T returnValue) {
        _errorMessage = errorMessage;
        _returnValue = returnValue;
    }

    // Gettera and Setters
    public String getErrorMessage() {
        return _errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        _errorMessage = errorMessage;
    }

    public T getReturnValue() {
        return _returnValue;
    }

    public void setReturnValue(T returnValue) {
        _returnValue = returnValue;
    }
}
