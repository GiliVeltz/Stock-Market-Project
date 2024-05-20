package ServiceLayer;
// Purpose: Response class for handling the response of the service layer.
public class Response {
    
    // Fields.
    public String ErrorMessage;
    public Object ReturnValue;

    // consructor.
    public Response() { }

    public Response(String msg, Object returnValue)
    {
        ErrorMessage = msg;
        this.ReturnValue = returnValue;
    }

    // Getter and Setter

    public String getErrorMessage()
    {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        ErrorMessage = errorMessage;
    }

    public Object getReturnValue()
    {
        return ReturnValue;
    }

    public void setReturnValue(Object returnValue)
    {
        this.ReturnValue = returnValue;
    }
}
