package UI.Model;

public class Response<T> {
    private String errorMessage;
    private T returnValue;

    // consructor.
    public Response() {
    }

    public Response(String errorMessage, T returnValue) {
        this.errorMessage = errorMessage; // Use 'this' to refer to instance variables
        this.returnValue = returnValue;   // Use 'this' to refer to instance variables
    }

    // Gettera and Setters
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }
}

