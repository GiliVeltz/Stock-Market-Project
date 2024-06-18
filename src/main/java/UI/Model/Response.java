package UI.Model;

public class Response<T> {
    private String errorMessage;
    private T returnValue;

    public Response(String errorMessage, T returnValue) {
        this.errorMessage = errorMessage;
        this.returnValue = returnValue;
    }

    // Getters and setters
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
