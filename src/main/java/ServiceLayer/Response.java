package ServiceLayer;

// Purpose: Response class for handling the response of the service layer.
public class Response {
    private String _errorMessage;
    private Object _returnValue;

    // consructor.
    public Response() {
    }

    public Response(String errorMessage, Object returnValue) {
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

    public Object getReturnValue() {
        return _returnValue;
    }

    public void setReturnValue(Object returnValue) {
        _returnValue = returnValue;
    }
}
