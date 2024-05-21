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
    public String get_errorMessage() {
        return _errorMessage;
    }

    public void set_errorMessage(String errorMessage) {
        _errorMessage = errorMessage;
    }

    public Object get_returnValue() {
        return _returnValue;
    }

    public void set_returnValue(Object returnValue) {
        _returnValue = returnValue;
    }
}
