package UI.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude
public class Response<T> {
    private String errorMessage;
    private T returnValue;

    // // Getters and setters
    // public String getErrorMessage() {
    //     return errorMessage;
    // }

    // public void setErrorMessage(String errorMessage) {
    //     this.errorMessage = errorMessage;
    // }

    // public String getReturnValue() {
    //     return returnValue;
    // }

    // public void setReturnValue(String returnValue) {
    //     this.returnValue = returnValue;
    // }

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
